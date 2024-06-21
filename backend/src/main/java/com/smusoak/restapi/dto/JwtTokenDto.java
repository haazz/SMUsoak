package com.smusoak.restapi.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public class JwtTokenDto {

    @Data
    @Builder
    public static class JwtAuthenticationResponse {
        String accessToken;
        String refreshToken;
    }

    @Data
    @RequiredArgsConstructor
    public static class RefreshTokenRequest {
        String refreshToken;
    }
}
