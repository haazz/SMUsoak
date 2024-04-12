package com.smusoak.restapi.config;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import com.smusoak.restapi.services.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


// Redis로 웹소켓에 접속 중인 사용자 세션을 관리
// { sessionId: [0] = userMail }
// { roomId: [0, 1, ...] = 채팅방에 접속 중인 sessionIds }
@Component
@RequiredArgsConstructor
public class CustomChannelInterceptor implements ChannelInterceptor {

    private final RedisService redisService;
    private final JwtService jwtService;
    private final UserService userService;
    private final ChatService chatService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @Value("${socket.session.expirationms}")
    private long sessionExpirationms;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(accessor.getCommand().equals(StompCommand.CONNECT)) {
            System.out.println("DetailLogMessage " + accessor.getDetailedLogMessage(message.getPayload()));
            System.out.println("Authorization: " + accessor.getFirstNativeHeader("Authorization"));

            // Authorization header 가져오고 검증
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
                System.out.println("config/CustomChannelInterceptor.java: JWT Token이 비어있거나 Bearer로 시작하지 않음 authHeader: " + authHeader);
                throw new CustomException(ErrorCode.JWT_TOKEN_INVALID);
            }
            // 헤더에서 Bearer 자르기
            String jwt = authHeader.substring(7);
            System.out.println("JWT: " + jwt.toString());
            // 토큰을 이용해 유저 메일 가져오기
            String userMail = jwtService.extractUserName(jwt);
            if(StringUtils.isEmpty(userMail)) {
                throw new CustomException(ErrorCode.JWT_TOKEN_INVALID);
            }
            // 유저 메일을 통해 유저 디테일을 가져와 토큰 유효성 검사
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userMail);
            if (!jwtService.isTokenValid(jwt, userDetails)) {
                throw new CustomException(ErrorCode.JWT_TOKEN_INVALID);
            }

            // 모든 검사를 통과 했다면 유저 세션을 redis에 저장
            // redis에 sessionId를 키로 갖는 list 저장 [0]: userMail
            String sessionId = accessor.getSessionId();
            redisService.deleteByKey(sessionId);
            redisService.setListOps(sessionId, userMail);
            redisService.setExpire(sessionId, sessionExpirationms);
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // log 찍기
        System.out.println("DetailLogMessage " + accessor.getDetailedLogMessage(message.getPayload()));
        if(accessor == null || accessor.getCommand() == null || accessor.getCommand().equals(StompCommand.CONNECT)) {
            return;
        }
        // 연결이 끊겼을 때 sessionId 삭제, 구독 중인 채팅 방에서 sessionId 삭제
        if(accessor.getCommand().equals(StompCommand.DISCONNECT)) {
            List<String> sessionDataList = redisService.getListOps(accessor.getSessionId());
            if(sessionDataList == null) {
                return;
            }
            redisService.deleteByKey(accessor.getSessionId());
        }
        // 채팅 방을 구독할 때 roomId 데이터에 sessionId를 업데이트
        else if(accessor.getCommand().equals(StompCommand.SUBSCRIBE)) {
            List<String> sessionList = redisService.getListOps(accessor.getDestination());
            // 현제 sessionId가 redis에 저장 안돼 있다면 sessionList에 add
            if(!sessionList.contains(accessor.getSessionId())) {
                sessionList.add(accessor.getSessionId());
            }
            // 채팅 방에 접속 중인 유저 업데이트
            redisService.deleteByKey(accessor.getDestination());
            redisService.setListOps(accessor.getDestination(), sessionList);
            redisService.setExpire(accessor.getDestination(), sessionExpirationms);
        }
        // 채팅 방 구독을 끊을 때 roomId 데이터에 sessionId를 제거
        else if(accessor.getCommand().equals(StompCommand.UNSUBSCRIBE)) {

            List<String> sessionList = redisService.getListOps(accessor.getDestination());
            System.out.println(sessionList);
            // 현제 sessionId가 roomList에 저장 안돼있다면 return
            if(!sessionList.contains(accessor.getSessionId())) {
                return;
            }
            sessionList.remove(accessor.getSessionId());
            // 채팅 방에 접속 중인 유저 업데이트
            redisService.deleteByKey(accessor.getDestination());
            if(sessionList.isEmpty()) {
                return;
            }
            redisService.setListOps(accessor.getDestination(), sessionList);
            redisService.setExpire(accessor.getDestination(), sessionExpirationms);
        }
        // 같은 방을 구독 중이지 않은 사용자들에게 FCM을 사용하여 알림 보내기
        // 방에서 sessionList를 가져와 sessionId들이 redis에 저장돼있는지 확인 필요
        else if(accessor.getCommand().equals(StompCommand.SEND)) {
            System.out.println(new String((byte[]) message.getPayload()));
            try {
                // chatroomService.getChatroom()을 해서 같은 채팅창 유저 리스트 가져오기
                // 본인을 제외한 유저가 websocket 같은 room에 접속중인지 session 조회 비교
                // 만약 없다면 Firebase로 알림 보내기

                // message payload를 통해 내용 가져오기
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new String((byte[]) message.getPayload()));
                String senderMail = (String) jsonObject.get("senderMail");
                String body = (String) jsonObject.get("message");
                String roomId = (String) jsonObject.get("roomId");
                System.out.println(senderMail + body + roomId);

                // 채팅방을 구독 중인 sessionIdList를 가져와 mailList로 변경
                // chatroom에 참여 중인 mail list를 가져와서
                // FCM으로 메시지 전송할 리스트를 추출
                System.out.println("WS:(send) accessor.Dest: " + accessor.getDestination() + "roomId: " + roomId);
                if(accessor.getDestination().equals("/topic/" + roomId)) {
                    throw new CustomException(ErrorCode.BAD_REQUEST);
                }
                List<String> sessionList = redisService.getListOps("/topic/" + roomId);
                System.out.println(sessionList);
                Set<String> chatRoomMails = chatService.getUserMailsByRoomId(Long.parseLong(roomId));
                for(String session: sessionList) {
                    String socketMail = redisService.getListOpsByIndex(session, 0);
                    chatRoomMails.remove(socketMail);
                }

                // chatRoomMails에 남아 있는 메일에 FCM 메시지를 전송
                for(String chatRoomMail: chatRoomMails) {
                    firebaseCloudMessageService.sendMessageByToken(senderMail, body, userService.getFcmTokenByMail(chatRoomMail));
                }
            } catch (ParseException e) {
                System.out.println("config/CustomChannelInterceptor: json parse 실패");
            } catch (FirebaseMessagingException e) {
                System.out.println("config/CustomChannelInterceptor: firebase 오류");
                throw new RuntimeException(e);
            }
        }
    }
}
