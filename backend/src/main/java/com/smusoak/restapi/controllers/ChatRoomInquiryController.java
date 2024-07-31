package com.smusoak.restapi.controllers;

import com.smusoak.restapi.models.ChatRoom;
import com.smusoak.restapi.services.ChatRoomInquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatRoomInquiryController {

    private final ChatRoomInquiryService chatRoomInquiryService;

    @Autowired
    public ChatRoomInquiryController(ChatRoomInquiryService chatRoomInquiryService) {
        this.chatRoomInquiryService = chatRoomInquiryService;
    }

    @GetMapping("/chatrooms")
    public ResponseEntity<List<ChatRoom>> getChatRooms(@RequestParam String mail1, @RequestParam String mail2) {
        List<ChatRoom> chatRooms = chatRoomInquiryService.getChatRoomsByEmails(mail1, mail2);
        return ResponseEntity.ok(chatRooms);
    }
}
//postmapping json list로 보내기