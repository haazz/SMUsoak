package com.smusoak.restapi.services;
import com.smusoak.restapi.dto.OpenChatDto;
import com.smusoak.restapi.models.*;
import com.smusoak.restapi.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OpenChatService {
    private final OpenChatRepository openChatRepository;
    private final UserRepository userRepository;

    private final ChatRoomRepository chatRoomRepository;

    public Long createOpenChat(OpenChatDto.OneToOneRequest request) {
        // 생성자 이메일로 사용자 정보 조회
        User creator = userRepository.findByMail(request.getMail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getMail()));

        User participant = userRepository.findByMail(request.getParticipant())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getParticipant()));


        LocalDateTime createdAt = request.getCreatedAt();

        OpenChat openChat = OpenChat.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .creator(creator)
                .participant(participant)
                .createdAt(createdAt)
                .build();

        OpenChat savedChat = openChatRepository.save(openChat);
        return savedChat.getId();
    }

    public OpenChat findById(Long chatId) {
        return openChatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("1:1 chat not found with ID: " + chatId));
    }
}
