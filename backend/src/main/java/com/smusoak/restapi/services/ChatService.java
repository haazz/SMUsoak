//package com.smusoak.restapi.services;
//
//import com.smusoak.restapi.dto.ChatDto;
//import com.smusoak.restapi.models.ChatRoom;
//import com.smusoak.restapi.models.Message;
//import com.smusoak.restapi.models.User;
//import com.smusoak.restapi.repositories.ChatRoomRepository;
//import com.smusoak.restapi.repositories.MessageRepository;
//import com.smusoak.restapi.response.CustomException;
//import com.smusoak.restapi.response.ErrorCode;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ChatService {
//    private final MessageRepository messageRepository;
//    private final ChatRoomRepository chatRoomRepository;
//
//    @Transactional(rollbackFor = Exception.class)
//    public void sendMessage(ChatDto.SendMessage message) {
//        messageRepository.save(Message.builder()
//                        .message(message.getMessage())
//                        .sender(User.builder().mail(message.getSenderMail()).build())
//                        .receiver(User.builder().mail(message.getReceiverMail()).build())
//                        .chatRoom(ChatRoom.builder().id(message.getRoomId()).build())
//                        .sendAt(LocalDateTime.now())
//                        .build());
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public Long joinChatRoom(ChatDto.ChatRoomRequest request) {
//        List<String> userMailList = request.getUserMailList().stream().distinct().collect(Collectors.toList());
//
//        if(userMailList.size() < 2) {
//            throw new CustomException(ErrorCode.MIN_USER_CREATE_CHATROOM);
//        }
//
//        List<User> userList = new ArrayList<User>();
//        for(String mail: userMailList) {
//            userList.add(User.builder().mail(mail).build());
//        }
//
//        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
//                .users(userList)
//                .build());
//        return chatRoom.getId();
//    }
//
////    @Transactional(readOnly = true)
////    public List<ChatRoomDto.Response> getRoomList(Long memberId) {
////        return chatRoomRepository.findListByMemberId(memberId).stream().map(ChatRoomDto.Response::of).collect(Collectors.toList());
////    }
////
////    @Transactional(readOnly = true)
////    public ChatRoomDto.Detail getRoomDetail(Long roomId) {
////        Optional<ChatRoomDto.Detail> room = chatRoomRepository.findById(roomId).map(ChatRoomDto.Detail::of);
////        return room.orElseThrow();
////    }
//
////    public List<ChatRoom> findListByMemberId(Long id) {
////        return entityManager.createQuery("select r from ChatRoom r where r.customer.id = :id or r.seller.id = :id", ChatRoom.class)
////                .setParameter("id", id)
////                .getResultList();
////    }
////
////    private boolean checkDuplicatiedChatRoom(List<Long> userIdList){
////        Optional<ChatRoom> chatRoomList = entityManager.createQuery("select r from ChatRoom r where r.customer.id = :customerId and r.seller.id = :sellerId and r.product.id = :productId", ChatRoom.class)
////                .setParameter("customerId", customerId)
////                .setParameter("sellerId", sellerId)
////                .setParameter("productId", productId)
////                .getResultList().stream().findFirst();
////        return chatRoomList.isPresent();
////    }
//}
