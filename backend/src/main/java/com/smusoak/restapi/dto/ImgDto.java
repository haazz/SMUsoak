package com.smusoak.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImgDto {

    @Data
    @NoArgsConstructor
    public static class UpdateUserImgRequest {
        @NotBlank
        private String mail;
        private MultipartFile file;
    }

    @Data
    @NoArgsConstructor
    public static class UserImgRequest {
        @NotBlank
        private String mail;
        private List<String> mailList;
    }

    @Data
    @Builder
    public static class UserImageResponse {
        private String mail;
        private String url;
        private String type;
    }
}
