package com.smusoak.restapi.dto;
import com.smusoak.restapi.models.ChatRoom;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ChatDto {
    @Getter
    public static class SendMessage {
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
    public static class chatRoomListDto {
        @NotBlank
        private String mail;
    }

    @Data
    @Builder
    public static class chatRoomListResponse {
        private List<ChatRoom> chatRoomList;
    }
}
