package com.smusoak.restapi.dto;
import com.smusoak.restapi.models.ChatRoom;
import com.smusoak.restapi.models.Message;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ChatDto {
    @Getter
    public static class SendMessageRequest {
        private String message;
        private String senderMail;
        private String receiverMail;
        private Long roomId;
    }

    @Getter
    public static class ChatRoomRequest {
        private List<String> userMailList;
    }

    @Data
    public static class ChatRoomListRequest {
        @NotBlank
        private String mail;
    }

    @Data
    @Builder
    public static class ChatRoomListResponse {
        private List<ChatRoom> chatRoomList;
    }

    @Data
    public static class ChatRoomMessagesRequest {
        @NotBlank
        private Long chatRoomId;
    }

    @Data
    @Builder
    public static class MessageListResponse {
        private List<Message> messageList;
    }
}
