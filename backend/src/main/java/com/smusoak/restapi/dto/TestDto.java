package com.smusoak.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TestDto {
    @Data
    @Builder
    public static class TestResponse {
        private String message;
    }

    @Data
    @Builder
    public static class FcmRequest {
        private String title;
        private String body;
        private String token;
    }
}
