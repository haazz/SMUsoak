package com.smusoak.restapi.dto;
import lombok.Builder;
import lombok.Getter;

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
}
