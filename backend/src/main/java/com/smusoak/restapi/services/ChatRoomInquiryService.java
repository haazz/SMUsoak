package com.smusoak.restapi.services;

import com.smusoak.restapi.models.*;
import com.smusoak.restapi.repositories.ChatRoomInquiryRepository;
import com.smusoak.restapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ChatRoomInquiryService {
    private final ChatRoomInquiryRepository chatRoomInquiryRepository;
    private final UserRepository userRepository;

    public List<ChatRoom> getChatRoomsByEmails(String mail1, String mail2) {
        Optional<User> user1 = userRepository.findByMail(mail1);
        Optional<User> user2 = userRepository.findByMail(mail2);

        if (user1.isEmpty()) {
            throw new RuntimeException("User with email " + mail1 + " not found");
        }
        else if (user2.isEmpty()) {
            throw new RuntimeException("User with email " + mail2 + " not found");
        }
        List<ChatRoom> chatRooms = chatRoomInquiryRepository.findChatRoomsByUsers(mail1, mail2);

        chatRooms.forEach(chatRoom -> {
            Hibernate.initialize(chatRoom.getUsers());
            chatRoom.getUsers().forEach(user -> Hibernate.initialize(user.getUserDetail()));
        });

        return chatRooms;
    }
}
// 지연 로딩 수정 (수정한 코드 user, chatroom, inquiryservice, userdetail)
//userlist도 join해서 가져오기
//@JsonIgnoreProperties 이거 붙어있는 곳은 다 수정한곳