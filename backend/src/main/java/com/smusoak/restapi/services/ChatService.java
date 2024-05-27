package com.smusoak.restapi.services;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.smusoak.restapi.dto.ChatDto;
import com.smusoak.restapi.models.ChatRoom;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.repositories.ChatRoomRepository;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final S3Service s3Service;

    // 웹소켓을 구독 중이지 않은 사용자들에게 FCM을 사용하여 알림 보내기
    public void sendMessage(ChatDto.SendMessageRequest request) throws FirebaseMessagingException {
        List<String> sessionList = redisService.getListOps("/topic/" + request.getRoomId());
        Set<String> chatRoomMails = this.getUserMailsByRoomId(request.getRoomId());
        if(sessionList == null || chatRoomMails == null) {
            System.out.println("/services/ChatService/sendMessage: session or chatroom not found");
            return;
        }
        for(String session: sessionList) {
            String socketMail = redisService.getListOpsByIndex(session, 0);
            chatRoomMails.remove(socketMail);
        }
        System.out.println("/services/ChatService/sendMessage roomId=" + request.getRoomId() + " sessionList=" + sessionList + " chatRoomMails=" + chatRoomMails);
        // chatRoomMails에 남아 있는 메일에 FCM 메시지를 전송
        for(String chatRoomMail: chatRoomMails) {
            firebaseCloudMessageService.sendMessageByToken(request.getSenderMail(), request.getMessage(), this.getFcmTokenByMail(chatRoomMail));
        }
    }

    private String getFcmTokenByMail(String mail) {
        return userRepository.findByMail(mail).get().getFcmToken();
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
    public List<ChatDto.ChatRoomInfo> getChatRoomList(String mail) {
        List<Object[]> objects = chatRoomRepository.findByUserMail(mail);
        List<ChatDto.ChatRoomInfo> chatRoomInfos = new ArrayList<>();

        for(Object[] object: objects) {
            String userMailsString = (String) object[1];
            List<String> userMailList = Arrays.asList(userMailsString.split(","));
            chatRoomInfos.add(ChatDto.ChatRoomInfo.builder()
                    .roomId((Long) object[0])
                    .mails(userMailList)
                    .build());
        }
        return chatRoomInfos;
    }

    public Set<String> getUserMailsByRoomId(Long roomId) {
        List<User> users = userRepository.findByChatRoomsId(roomId);
        Set<String> mails = new HashSet<>();
        for(User user: users) {
            String mail = user.getMail();
            if(!mail.isBlank()) {
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

    public String updateImg(String roomId, MultipartFile file) {
        String fileName = roomId + "/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        s3Service.updateImg(fileName, file);
        return fileName;
    }
}
