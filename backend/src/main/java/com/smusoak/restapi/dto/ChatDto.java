package com.smusoak.restapi.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ChatDto {
    @Data
    @NoArgsConstructor
    public static class SendMessageRequest {
        private String message;
        @NotBlank
        private String senderMail;
        private String senderName;
        // 0 or null = text
        // 1 = image
        private String flag;
        private Long roomId;
        private String time;
    }

    @Data
    @NoArgsConstructor
    public static class ChatRoomRequest {
        private List<String> userMailList;
    }

    @Data
    @NoArgsConstructor
    public static class ChatRoomListRequest {
        @NotBlank
        private String mail;
    }

    @Data
    @Builder
    public static class ChatRoomInfo {
        private Long roomId;
        private List<String> mails;
    }

    @Data
    @NoArgsConstructor
    public static class ChatRoomMessagesRequest {
        @NotBlank
        private Long chatRoomId;
    }
}
