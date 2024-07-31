package com.smusoak.restapi.dto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class OpenChatDto {

    @Getter
    @Data
    public static class OneToOneRequest {
        private String title;
        private String description;

        private String mail;
        private LocalDateTime createdAt;
    }
    @Builder
    @Data
    public static class OneToOneResponse {
        private Long chatId;
    }
}
