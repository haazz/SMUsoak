package com.smusoak.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class UserDto {
    @Data
    @NoArgsConstructor
    public static class SendCodeRequest {
        @NotBlank
        private String mail;
    }

    @Data
    @NoArgsConstructor
    public static class MailVerificationRequest {
        @NotBlank
        private String mail;
        @NotBlank
        private String authCode;
    }

    @Data
    @NoArgsConstructor
    public static class SignupRequest {
        @NotBlank
        private String mail;
        @NotBlank
        private String password;
        private String nickname;
        private Integer age;
        private char gender;
        private String mbti;
    }

    @Data
    @NoArgsConstructor
    public static class SigninRequest {
        @NotBlank
        private String mail;
        @NotBlank
        private String password;
    }

    @Data
    @NoArgsConstructor
    public static class UpdateUserDetailsRequest {
        @NotBlank
        private String mail;
        private Integer age;
        private char gender;
        private String mbti;
    }
}
