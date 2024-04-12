package com.smusoak.restapi.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.smusoak.restapi.dto.ChatDto;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.models.ChatRoom;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // Mapped as app/send
    @MessageMapping("/send")
    public ResponseEntity<ApiResponseEntity> send(@Payload ChatDto.SendMessageRequest request) throws FirebaseMessagingException {
        messagingTemplate.convertAndSend("/topic/" + request.getRoomId(), request);
        chatService.sendMessage(request);
        return ApiResponseEntity.toResponseEntity();
    }

    @GetMapping("/room/list/{mail}")
    public ResponseEntity<ApiResponseEntity> chatRoomList(@PathVariable String mail) {
        return ApiResponseEntity.toResponseEntity(chatService.getChatRoomList(mail));
    }

//    @GetMapping("/room/messages")
//    public ResponseEntity<ApiResponseEntity> getChatRoomMessages(@RequestBody ChatDto.ChatRoomMessagesRequest request) {
//        return chatService.getRoomMessages(request);
//    }
}
