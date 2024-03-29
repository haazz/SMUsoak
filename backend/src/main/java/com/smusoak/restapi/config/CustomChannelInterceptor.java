package com.smusoak.restapi.config;

import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import com.smusoak.restapi.services.JwtService;
import com.smusoak.restapi.services.RedisService;
import com.smusoak.restapi.services.UserService;
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

@Component
@RequiredArgsConstructor
public class CustomChannelInterceptor implements ChannelInterceptor {

    private final RedisService redisService;
    private final JwtService jwtService;
    private final UserService userService;

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
            // redis에 sessionId를 키로 갖는 list 저장 [0]: userMail [1]: roomId
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
            if(sessionDataList.size() <= 1) {
                return;
            }
            // roomId에 저장되어 있는 sessionId 제거
            List<String> sessionList = redisService.getListOps(sessionDataList.get(1));
            sessionList.remove(accessor.getSessionId());
            // roomId에 저장된 sessionId 리스트를 제거 혹은 재 업로드
            redisService.deleteByKey(sessionDataList.get(1));
            if(sessionList.isEmpty()) {
                return;
            }
            redisService.setListOps(sessionDataList.get(1), sessionList);
            redisService.setExpire(sessionDataList.get(1), sessionExpirationms);
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
            // sessionId에 채팅 방 업데이트
            List<String> sessionDataList = redisService.getListOps(accessor.getSessionId());
            redisService.deleteByKey(accessor.getSessionId());
            redisService.setListOps(accessor.getSessionId(), sessionDataList.get(0), accessor.getDestination());
            redisService.setExpire(accessor.getSessionId(), sessionExpirationms);
        }
        // 채팅 방 구독을 끊을 때 roomId 데이터에 sessionId를 제거
        else if(accessor.getCommand().equals(StompCommand.UNSUBSCRIBE)) {
            // sessionId에 채팅 방 업데이트
            List<String> sessionDataList = redisService.getListOps(accessor.getSessionId());
            redisService.deleteByKey(accessor.getSessionId());
            redisService.setListOps(accessor.getSessionId(), sessionDataList.get(0), accessor.getDestination());
            redisService.setExpire(accessor.getSessionId(), sessionExpirationms);

            List<String> sessionList = redisService.getListOps(accessor.getDestination());
            System.out.println(sessionList);
            // 현제 sessionId가 redis에 저장 안돼 있다면 sessionList에 add
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
        else if(accessor.getCommand().equals(StompCommand.SEND)) {
            System.out.println(new String((byte[]) message.getPayload()));
            try {
                // chatroomService.getChatroom()을 해서 같은 채팅창 유저 리스트 가져오기
                // 본인을 제외한 유저가 websocket 같은 room에 접속중인지 session 조회 비교
                // 만약 없다면 Firebase로 알림 보내기
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new String((byte[]) message.getPayload()));
                System.out.println(jsonObject.get("message"));
                List<String> sessionList = redisService.getListOps("/topic/" + (String) jsonObject.get("roomId"));
                System.out.println(sessionList);
            }
            catch (Exception e) {
                System.out.println("config/CustomChannelInterceptor: json parse 실패");
            }
        }
    }
}
