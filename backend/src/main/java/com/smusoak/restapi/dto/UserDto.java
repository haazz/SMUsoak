package com.smusoak.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class UserDto {
    @Data
    @NoArgsConstructor
    public static class sendAuthCodeDto {
        @NotBlank
        private String mail;
    }

    @Data
    @NoArgsConstructor
    public static class mailVerificationDto {
        @NotBlank
        private String mail;
        @NotBlank
        private String authCode;
    }

    @Data
    @NoArgsConstructor
    public static class createUserDto {
        @NotBlank
        private String mail;
        @NotBlank
        private String password;
        private String nickname;
        private Integer age;
        private char gender;
        private String major;
    }

    @Data
    @NoArgsConstructor
    public static class signinDto {
        @NotBlank
        private String mail;
        @NotBlank
        private String password;
    }

    @Data
    @NoArgsConstructor
    public static class updateUserDetailsDto {
        @NotBlank
        private String mail;
        private Integer age;
        private char gender;
        private String mbti;
    }

    @Data
    @NoArgsConstructor
    public static class updateUserImg {
        @NotBlank
        private String mail;
        private MultipartFile file;
    }

    @Data
    @NoArgsConstructor
    public static class getUserImg {
        @NotBlank
        private String mail;
        private List<String> mailList;
    }

    @Data
    @Builder
    public static class userImageResponse {
        private String mail;
        private String url;
        private String type;
    }

}
