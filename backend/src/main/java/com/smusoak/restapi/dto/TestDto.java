package com.smusoak.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TestDto {
    @Data
    @Builder
    public static class responseAuth {
        private String message;
    }
}
