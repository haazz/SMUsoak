package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.ChatDto;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // Mapped as app/send
    @MessageMapping("/send")
    public ResponseEntity<ApiResponseEntity> send(@Payload ChatDto.SendMessage request) {
        messagingTemplate.convertAndSend("/topic/" + request.getRoomId(), request);
        return ApiResponseEntity.toResponseEntity();
    }

    @GetMapping("/roomList")
    public ResponseEntity<ApiResponseEntity> chatRoomList(ChatDto.chatRoomListDto request) {
        return chatService.getChatRoomList(request);
    }

    @GetMapping("/roomMessages")
    public ResponseEntity<ApiResponseEntity> getChatRoomMessages(ChatDto.chatRoomMessagesDto request) {
        return chatService.getRoomMessages(request);
    }
}
