package com.smusoak.restapi.services;
import com.smusoak.restapi.dto.OpenGroupChatDto;
import com.smusoak.restapi.models.*;
import com.smusoak.restapi.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OpenGroupChatService {
    private final OpenGroupChatRepository openGroupChatRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public Long createGroupChat(OpenGroupChatDto.GroupRequest request) {
        // 그룹 채팅방 생성
        User creator = userRepository.findByMail(request.getMail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getMail()));

        LocalDateTime createdAt = request.getCreatedAt();

        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder().build());
        chatRoom.setUserList(new ArrayList<>());
        // 생성된 채팅방에 생성자를 추가
        chatRoom.getUserList().add(creator);

        OpenGroupChat groupChat = OpenGroupChat.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .creator(creator)
                .createdAt(createdAt)
                .chatRoom(chatRoom)
                .build();

        // 그룹 채팅방 저장
        openGroupChatRepository.save(groupChat);

        return groupChat.getId();
    }

    public OpenGroupChat findById(Long chatId) {
        return openGroupChatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Group chat not found with ID: " + chatId));
    }
}

