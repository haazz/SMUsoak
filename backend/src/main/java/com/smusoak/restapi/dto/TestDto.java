package com.smusoak.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class TestDto {
    @Data
    @Builder
    public static class TestResponse {
        private String message;
    }

    @Data
    @NoArgsConstructor
    public static class FcmRequest {
        private String title;
        private String body;
        private String token;
    }

    @Data
    @NoArgsConstructor
    public static class ChatRoomRequest {
        private List<String> mails;
    }

    @Data
    @NoArgsConstructor
    public static class ChatRoomResponse {
        private Long chatRoomId;
    }
}
