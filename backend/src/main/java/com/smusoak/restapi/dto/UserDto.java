package com.smusoak.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
        private String major;
    }
}
