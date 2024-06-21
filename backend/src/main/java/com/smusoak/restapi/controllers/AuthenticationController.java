package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.JwtTokenDto;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/mail/send-code")
    public ResponseEntity<ApiResponseEntity> sendAuthCode(@Valid @RequestBody UserDto.SendCodeRequest request) throws Exception{
        authenticationService.sendCodeToMail(request);
        return ApiResponseEntity.toResponseEntity();
    }

    @PostMapping("/mail/verification")
    public ResponseEntity<ApiResponseEntity> mailVerification(@Valid @RequestBody UserDto.MailVerificationRequest request) throws Exception{
        authenticationService.verifiedCode(request);
        return ApiResponseEntity.toResponseEntity();
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseEntity> createUser(@Valid @RequestBody UserDto.SignupRequest request) throws Exception{
        System.out.println(request);
        JwtTokenDto.JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.createUser(request);
        return ApiResponseEntity.toResponseEntity(
                jwtAuthenticationResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponseEntity> signin(@Valid @RequestBody UserDto.SigninRequest request) throws Exception {
        JwtTokenDto.JwtAuthenticationResponse jwtAuthenticationResponse =  authenticationService.signin(request);
        return ApiResponseEntity.toResponseEntity(
                jwtAuthenticationResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseEntity> refreshToken(@Valid  @RequestBody JwtTokenDto.RefreshTokenRequest request) {
        JwtTokenDto.JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.refreshToken(request.getRefreshToken());
        return ApiResponseEntity.toResponseEntity(
                jwtAuthenticationResponse);
    }
}
