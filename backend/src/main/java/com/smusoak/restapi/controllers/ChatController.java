package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.ChatDto;
//import com.smusoak.restapi.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
//    private final ChatService chatService;

    // Mapped as app/send
    @MessageMapping("/send")
    @SendTo("/topic/chat")
    public ChatDto.SendMessage send(ChatDto.SendMessage request) {
        return request;
//         chatService.sendMessage(request);
//         messagingTemplate.convertAndSend("/topic/chat/" + request.getReceiverId(), request);
    }
}
