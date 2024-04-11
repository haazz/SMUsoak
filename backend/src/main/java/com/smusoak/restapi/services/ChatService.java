package com.smusoak.restapi.services;

import com.smusoak.restapi.dto.ChatDto;
import com.smusoak.restapi.models.ChatRoom;
import com.smusoak.restapi.models.Message;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.repositories.ChatRoomRepository;
import com.smusoak.restapi.repositories.MessageRepository;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(ChatDto.SendMessageRequest message) {
        messageRepository.save(Message.builder()
                        .message(message.getMessage())
                        .sender(userRepository.findByMail(message.getSenderMail()).get())
                        .receiver(userRepository.findByMail(message.getReceiverMail()).get())
                        .chatRoom(ChatRoom.builder().id(message.getRoomId()).build())
                        .sendAt(LocalDateTime.now())
                        .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createChatRoom(ChatDto.ChatRoomRequest request) {
        //List<String> userMailList = request.getUserMailList().stream().distinct().collect(Collectors.toList());
        List<String> userMailList = request.getUserMailList();
        if(userMailList.size() < 2) {
            throw new CustomException(ErrorCode.MIN_USER_CREATE_CHATROOM);
        }

        Set<User> userList = new HashSet<>();
        for(String mail: userMailList) {
            userList.add(userRepository.findByMail(mail).get());
        }

        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .users(userList)
                .build());
        return chatRoom.getId();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseEntity> getChatRoomList(ChatDto.ChatRoomListRequest request) {
        return ApiResponseEntity.toResponseEntity(ChatDto.ChatRoomListResponse.builder()
                .chatRoomList(chatRoomRepository.findByUsersMail(request.getMail()))
                .build());
        // return chatRoomRepository.findListByMemberId(memberId).stream().map(ChatRoomDto.Response::of).collect(Collectors.toList());
    }

    public ResponseEntity<ApiResponseEntity> getRoomMessages(ChatDto.ChatRoomMessagesRequest request) {
        return ApiResponseEntity.toResponseEntity(ChatDto.MessageListResponse.builder()
                .messageList(messageRepository.findByChatRoomId(request.getChatRoomId()))
                .build());
    }

    public List<String> getUserMailsByRoomId(Long roomId) {
        List<User> users = userRepository.findByChatRoomsId(roomId);
        List<String> mails = new ArrayList<>();
        for(User user: users) {
            String mail = user.getMail();
            if(!mail.isEmpty()) {
                mails.add(mail);
            }
        }
        return mails;
    }

    public Long putUserToChatRoom(List<String> mails) {
        Set<User> userList = new HashSet<>();
        for(String mail: mails) {
            Optional<User> user = userRepository.findByMail(mail);
            if(user.isPresent()) {
                userList.add(user.get());
            }
        }
        Long roomId = chatRoomRepository.save(ChatRoom.builder().users(userList).build()).getId();
        return roomId;
    }

//    @Transactional(readOnly = true)
//    public ChatRoomDto.Detail getRoomDetail(Long roomId) {
//        Optional<ChatRoomDto.Detail> room = chatRoomRepository.findById(roomId).map(ChatRoomDto.Detail::of);
//        return room.orElseThrow();
//    }

//    public List<ChatRoom> findListByMemberId(Long id) {
//        return entityManager.createQuery("select r from ChatRoom r where r.customer.id = :id or r.seller.id = :id", ChatRoom.class)
//                .setParameter("id", id)
//                .getResultList();
//    }
//
//    private boolean checkDuplicatiedChatRoom(List<Long> userIdList){
//        Optional<ChatRoom> chatRoomList = entityManager.createQuery("select r from ChatRoom r where r.customer.id = :customerId and r.seller.id = :sellerId and r.product.id = :productId", ChatRoom.class)
//                .setParameter("customerId", customerId)
//                .setParameter("sellerId", sellerId)
//                .setParameter("productId", productId)
//                .getResultList().stream().findFirst();
//        return chatRoomList.isPresent();
//    }
}
