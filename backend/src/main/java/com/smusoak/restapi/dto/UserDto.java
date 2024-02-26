package com.smusoak.restapi.dto;

import lombok.*;

public class UserDto {
    @Data
    @NoArgsConstructor
    public static class sendAuthCodeDto {
        private String mail;
    }

    @Data
    @NoArgsConstructor
    public static class mailVerificationDto {
        private String mail;
        private String token;
    }

    @Data
    @NoArgsConstructor
    public static class createUserDto {
        private String mail;
        private String password;
        private Integer age;
        private char gender;
        private String major;
    }

    @Data
    @NoArgsConstructor
    public static class signinDto {
        private String mail;
        private String password;
    }

    @Data
    @NoArgsConstructor
    public static class updateUserDetailsDto {
        private String mail;
        private Integer age;
        private char gender;
        private String major;
    }
}
