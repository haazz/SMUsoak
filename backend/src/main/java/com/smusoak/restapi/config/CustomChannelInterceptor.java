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

    @Value("${socket.session.expirationms}")
    private long sessionExpirationms;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // log 찍기
        System.out.println("DetailLogMessage " + accessor.getDetailedLogMessage(message.getPayload()));

        if(accessor.getCommand().equals(StompCommand.CONNECT)) {
            // Authorization header 가져오고 검증
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            System.out.println("Authorization: " + authHeader);
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
        if(accessor == null || accessor.getCommand() == null) {
            return;
        }
        // 연결이 끊겼을 때 sessionId 삭제, 구독 중인 채팅 방에서 sessionId 삭제
        if(accessor.getCommand().equals(StompCommand.DISCONNECT)) {
            List<String> sessionInfos = redisService.getListOps(accessor.getSessionId());
            List<String> sessionList;

            if(sessionInfos == null) {
                return;
            }
            if(sessionInfos.size() < 2) {
                redisService.deleteByKey(accessor.getSessionId());
                return;
            }
            // 구독 중인 채팅방에서 세션 아이디 삭제
            sessionList = redisService.getListOps(sessionInfos.get(1));
            if(sessionList != null) {
                sessionList.remove(accessor.getSessionId());
                redisService.deleteByKey(sessionInfos.get(1));
                if(!sessionList.isEmpty()) {
                    redisService.setListOps(sessionInfos.get(1), sessionList);
                    redisService.setExpire(sessionInfos.get(1), sessionExpirationms);
                }
            }
            redisService.deleteByKey(accessor.getSessionId());
        }
        // 채팅 방을 구독할 때 roomId 데이터에 sessionId를 업데이트
        else if(accessor.getCommand().equals(StompCommand.SUBSCRIBE)) {
            List<String> sessionInfos = new ArrayList<>();
            List<String> sessionList = redisService.getListOps(accessor.getDestination());

            // sessionId에 subscribe 정보 업데이트
            String mail = redisService.getListOpsByIndex(accessor.getSessionId(), 0);
            if(mail == null) {
                System.out.println("SUBSCRIBE: Session not found");
                return;
            }
            sessionInfos.add(mail);
            sessionInfos.add(accessor.getDestination());
            redisService.deleteByKey(accessor.getSessionId());
            redisService.setListOps(accessor.getSessionId(), sessionInfos);
            redisService.setExpire(accessor.getSessionId(), sessionExpirationms);

            // 현제 sessionId가 redis에 저장 안돼 있다면 sessionList에 add
            // 채팅 방에 접속 중인 유저 업데이트
            if(sessionList == null) {
                sessionList = new ArrayList<>();
                sessionList.add(accessor.getSessionId());
            }
            else if(!sessionList.contains(accessor.getSessionId())) {
                sessionList.add(accessor.getSessionId());
            }
            redisService.deleteByKey(accessor.getDestination());
            redisService.setListOps(accessor.getDestination(), sessionList);
            redisService.setExpire(accessor.getDestination(), sessionExpirationms);
        }
    }
}
