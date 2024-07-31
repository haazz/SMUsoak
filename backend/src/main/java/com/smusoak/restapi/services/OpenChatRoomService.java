package com.smusoak.restapi.services;

import com.smusoak.restapi.dto.OpenChatDto;
import com.smusoak.restapi.dto.OpenGroupChatDto;
import com.smusoak.restapi.models.*;
import com.smusoak.restapi.repositories.ChatRoomRepository;
import com.smusoak.restapi.repositories.OpenGroupChatRepository;
import com.smusoak.restapi.repositories.OpenChatRepository;
import com.smusoak.restapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
=======
import java.util.*;
>>>>>>> feature/matching-fix

@Service
@RequiredArgsConstructor
public class OpenChatRoomService {
    private final OpenChatRepository openChatRepository;
    private final UserRepository userRepository;
    private final OpenGroupChatRepository openGroupChatRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 1:1 대화 생성
    public Long createOpenChat(OpenChatDto.OneToOneRequest request) {
        User creator = userRepository.findByMail(request.getMail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getMail()));

        LocalDateTime createdAt = request.getCreatedAt();

        OpenChat openChat = OpenChat.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .creator(creator)
                .createdAt(createdAt)
                .build();

        OpenChat savedChat = openChatRepository.save(openChat);
        return savedChat.getId();
    }

    // 그룹 대화 생성
    public Long createGroupChat(OpenGroupChatDto.GroupRequest request) {
        User creator = userRepository.findByMail(request.getMail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getMail()));

        LocalDateTime createdAt = request.getCreatedAt();
<<<<<<< HEAD

        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder().build());
        chatRoom.setUsers(new HashSet<>());
        chatRoom.getUsers().add(creator); // 생성자를 채팅방에 추가
=======
        Set<User> users = new HashSet<>();
        users.add(creator);
        ChatRoom chatRoom  = ChatRoom.builder().users(users).build();
        chatRoomRepository.save(chatRoom);
//        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder().build());
//        chatRoom.setUserList(new ArrayList<>());
//        chatRoom.getUserList().add(creator); // 생성자를 채팅방에 추가
>>>>>>> feature/matching-fix

        OpenGroupChat groupChat = OpenGroupChat.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .creator(creator)
                .createdAt(createdAt)
                .chatRoom(chatRoom)
                .build();

        openGroupChatRepository.save(groupChat);

        return groupChat.getId();
    }

    // 1:1 대화 조회
    public List<OpenChat> getAllOpenChats() {
        return openChatRepository.findAll();
    }

    // 그룹 대화 조회
    public List<OpenGroupChat> getAllGroupChats() {
        return openGroupChatRepository.findAll();
    }

    // 이메일로 1:1 대화 조회
    public List<OpenChat> getOpenChatsByCreatorMail(String creatorMail) {
        Optional<User> creator = userRepository.findByMail(creatorMail);
        if (!creator.isPresent()) {
            throw new RuntimeException("User not found with email: " + creatorMail);
        }
        // 해당 사용자가 생성한 1:1 오픈 채팅방 목록을 가져옴
        return openChatRepository.findByCreatorMail(creator.get().getMail());
    }

    // 이메일로 그룹 대화 조회
    public List<OpenGroupChat> getGroupChatsByCreatorMail(String creatorMail) {
        Optional<User> creator = userRepository.findByMail(creatorMail);
        if (!creator.isPresent()) {
            throw new RuntimeException("User not found with email: " + creatorMail);
        }
        // 해당 사용자가 생성한 그룹 오픈 채팅방 목록을 가져옴
        return openGroupChatRepository.findByCreatorMail(creator.get().getMail());
    }
    //1대1 및 그룹 생성자 채팅방 삭제
    public void deleteOpenChatByCreatorMail(String creatorMail, Long chatId) {
        Optional<User> creator = userRepository.findByMail(creatorMail);
        if (!creator.isPresent()) {
            throw new RuntimeException("User not found with email: " + creatorMail);
        }
        // 해당 사용자가 생성한 채팅방인지 확인
        Optional<OpenChat> chatRoom = openChatRepository.findByIdAndCreatorId(chatId, creator.get().getId());
        if (!chatRoom.isPresent()) {
            throw new RuntimeException("1:1 chat room with ID " + chatId + " not found or not created by user with email " + creatorMail);
        }
        // 채팅방 삭제
        openChatRepository.delete(chatRoom.get());
    }

    public void deleteGroupChatByCreatorMail(String creatorMail, Long chatId) {
        Optional<User> creator = userRepository.findByMail(creatorMail);
        if (!creator.isPresent()) {
            throw new RuntimeException("User not found with email: " + creatorMail);
        }
        // 해당 사용자가 생성한 채팅방인지 확인
        Optional<OpenGroupChat> chatRoom = openGroupChatRepository.findByIdAndCreatorId(chatId, creator.get().getId());
        if (!chatRoom.isPresent()) {
            throw new RuntimeException("Group chat room with ID " + chatId + " not found or not created by user with email " + creatorMail);
        }
        // 채팅방 삭제
        openGroupChatRepository.delete(chatRoom.get());
    }
}
//creator == null
//openchatController
