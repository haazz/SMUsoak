package com.smusoak.restapi.services;
import com.smusoak.restapi.dto.OpenChatDto;
import com.smusoak.restapi.dto.OpenGroupChatDto;
import com.smusoak.restapi.models.*;
import com.smusoak.restapi.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OpenChatService {
//    private final OpenChatRepository openChatRepository;
//    private final UserRepository userRepository;
//    private final OpenGroupChatRepository openGroupChatRepository;
//
//
//
//    //1대1 및 그룹 전체조회
//    public List<OpenChat> getAllOpenChats() { return openChatRepository.findAll();}
//
//
//    public List<OpenGroupChat> getAllGroupChats() {
//        return openGroupChatRepository.findAll();
//    }
//
    //1대1 및 그룹 생성자 메일로 조회
//    public List<OpenChat> getOpenChatsByCreatorMail(String creatorMail) {
//        Optional<User> creator = userRepository.findByMail(creatorMail);
//        if (!creator.isPresent()) {
//            throw new RuntimeException("User not found with email: " + creatorMail);
//        }
//        // 해당 사용자가 생성한 오픈 채팅방 목록을 가져옴
//        List<OpenChat> oneToOneChats = openChatRepository.findByCreatorMail(creator.get().getMail());
//        return oneToOneChats;
//    }
//
//    public List<OpenGroupChat> getGroupChatsByCreatorMail(String creatorMail) {
//        Optional<User> creator = userRepository.findByMail(creatorMail);
//        if (!creator.isPresent()) {
//            throw new RuntimeException("User not found with email: " + creatorMail);
//        }
//        // 해당 사용자가 생성한 그룹 오픈 채팅방 목록을 가져옴
//        List<OpenGroupChat> groupChats = openGroupChatRepository.findByCreatorMail(creator.get().getMail());
//        return groupChats;
//    }
//
//
//    //1대1 및 그룹 생성자 채팅방 삭제
//    public void deleteOpenChatRoomByCreatorMail(String creatorMail, Long chatId) {
//        Optional<User> creator = userRepository.findByMail(creatorMail);
//        if (!creator.isPresent()) {
//            throw new RuntimeException("User not found with email: " + creatorMail);
//        }
//        // 해당 사용자가 생성한 채팅방인지 확인
//        Optional<OpenChat> chatRoom = openChatRepository.findByIdAndCreatorId(chatId, creator.get().getId());
//        if (!chatRoom.isPresent()) {
//            throw new RuntimeException("1:1 chat room with ID " + chatId + " not found or not created by user with email " + creatorMail);
//        }
//        // 채팅방 삭제
//        openChatRepository.delete(chatRoom.get());
//    }
//
//    public void deleteGroupChatRoomByCreatorMail(String creatorMail, Long chatId) {
//        Optional<User> creator = userRepository.findByMail(creatorMail);
//        if (!creator.isPresent()) {
//            throw new RuntimeException("User not found with email: " + creatorMail);
//        }
//        // 해당 사용자가 생성한 채팅방인지 확인
//        Optional<OpenGroupChat> chatRoom = openGroupChatRepository.findByIdAndCreatorId(chatId, creator.get().getId());
//        if (!chatRoom.isPresent()) {
//            throw new RuntimeException("Group chat room with ID " + chatId + " not found or not created by user with email " + creatorMail);
//        }
//        // 채팅방 삭제
//        openGroupChatRepository.delete(chatRoom.get());
//    }
}
// 크리에이터 부분 null 오류 수정
// groupchat 부분 오류 디버깅