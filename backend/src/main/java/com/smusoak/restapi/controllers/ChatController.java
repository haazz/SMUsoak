package com.smusoak.restapi.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.smusoak.restapi.dto.ChatDto;
import com.smusoak.restapi.dto.ImgDto;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.models.ChatRoom;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.ChatService;
import com.smusoak.restapi.services.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final S3Service s3Service;

    // Mapped as app/send
    @MessageMapping("/send")
    public void send(@Payload ChatDto.SendMessageRequest request) throws FirebaseMessagingException {
        System.out.println("/controllers/ChatController/send: " + request.toString());
        messagingTemplate.convertAndSend("/topic/" + request.getRoomId(), request);
        chatService.sendMessage(request);
    }

    @GetMapping("/room/list/{mail}")
    public ResponseEntity<ApiResponseEntity> chatRoomList(@PathVariable String mail) {
        return ApiResponseEntity.toResponseEntity(chatService.getChatRoomList(mail));
    }

    @PostMapping(value = "/update/img", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponseEntity> updateChatImg(@RequestPart(value="info", required=true) ImgDto.UpdateChatImgRequest request,
                                                           @RequestPart(value="file", required=true) MultipartFile file) {
        String fileName = chatService.updateImg(request.getRoomId(), file);
        return ApiResponseEntity.toResponseEntity(ImgDto.ImgNameResponse.builder()
                .fileName(fileName)
                .build());
    }
}
