package com.smusoak.restapi.config;

import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import com.smusoak.restapi.services.JwtService;
import com.smusoak.restapi.services.RedisService;
import com.smusoak.restapi.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CustomChannelInterceptor implements ChannelInterceptor {

    private final RedisService redisService;
    private final JwtService jwtService;
    private final UserService userService;

    @Value("${socket.session.expirationms}")
    private long sessionExpirationms;

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("command: " + accessor.getCommand());
        System.out.println("user: " + accessor.getUser());
        System.out.println("Authorization: " + accessor.getFirstNativeHeader("Authorization"));
        System.out.println("destination: " + accessor.getDestination());


        if(accessor.getCommand().equals("CONNECT")) {
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
            String sessionId = accessor.getSessionId();
            redisService.deleteByKey(sessionId);
            redisService.setValues(sessionId, userMail);
            redisService.setExpire(sessionId, sessionExpirationms);
        }
        else if(accessor.getCommand().equals("DISCONNECT")) {
            redisService.deleteByKey(accessor.getSessionId());
        }
        else if(accessor.getCommand().equals("SUBSCRIBE")) {
            List<String> sessionList = redisService.getListOps(accessor.getDestination());
            // 현제 sessionId가 redis에 저장 안돼 있다면 sessionList에 add
            if(!sessionList.contains(accessor.getSessionId())) {
                sessionList.add(accessor.getSessionId());
            }
            // redis data 업데이트
            redisService.deleteByKey(accessor.getDestination());
            redisService.setListOps(accessor.getDestination(), sessionList);
        }
        else if(accessor.getCommand().equals("UNSUBSCRIBE")) {
            List<String> sessionList = redisService.getListOps(accessor.getDestination());
            // 현제 sessionId가 redis에 저장 안돼 있다면 sessionList에 add
            if(!sessionList.contains(accessor.getSessionId())) {
                return;
            }
            sessionList.remove(accessor.getSessionId());
            // redis data 업데이트
            redisService.deleteByKey(accessor.getDestination());
            redisService.setListOps(accessor.getDestination(), sessionList);
        }
        else if(accessor.getCommand().equals("SEND")) {
            List<String> sessionList = redisService.getListOps(accessor.getDestination());
            // chatroomService.getChatroom()을 해서 같은 채팅창 유저 리스트 가져오기
            // 본인을 제외한 유저가 websocket 같은 room에 접속중인지 session 조회 비교
            // 만약 없다면 Firebase로 알림 보내기
        }
    }
    private String extractMessageContent(Message<?> message) {
        // 메시지 내용 추출 로직을 여기에 추가
        // 예를 들어, 특정 헤더를 이용하거나 메시지 페이로드를 확인할 수 있습니다.

        // 예시: 페이로드가 문자열인 경우
        System.out.println("message class:" + message.getClass());
        System.out.println("payload class:" + message.getPayload().getClass());
        Object payload = message.getPayload();
        if (payload instanceof String) {
            return (String) payload;
        }

        // 다른 유형의 페이로드에 대한 처리를 추가할 수 있습니다.

        return "Unable to extract message content";
    }
}
