package com.smusoak.restapi.config;

import com.smusoak.restapi.services.RedisService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

public class CustomChannelInterceptor implements ChannelInterceptor {

    RedisService redisService;

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("command: " + accessor.getCommand());
        System.out.println("user: " + accessor.getUser());
        System.out.println("Authorization: " + accessor.getFirstNativeHeader("Authorization"));
        if(accessor.getCommand().equals("SUBSCRIBE")) {

        }
        else if(accessor.getCommand().equals("DISCONNECT")) {

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
