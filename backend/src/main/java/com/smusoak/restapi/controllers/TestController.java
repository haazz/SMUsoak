package com.smusoak.restapi.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.smusoak.restapi.dto.ChatDto;
import com.smusoak.restapi.dto.TestDto;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @GetMapping("/hello")
    public ResponseEntity<ApiResponseEntity> hello() {
        return ApiResponseEntity.toResponseEntity(
                TestDto.TestResponse.builder().message("Hello!").build());
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseEntity> userEndPoint() {
        return ApiResponseEntity.toResponseEntity(
                TestDto.TestResponse.builder().message("ONLY user can see this").build());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseEntity> adminEndPoint() {
        return ApiResponseEntity.toResponseEntity(
                TestDto.TestResponse.builder().message("ONLY admin can see this").build());
    }

    @PostMapping("/firebase")
    public ResponseEntity<ApiResponseEntity> sendFcm(@RequestBody TestDto.FcmRequest request) throws FirebaseMessagingException {
        System.out.println(request);
        firebaseCloudMessageService.sendMessageByToken(request.getTitle(), request.getBody(), request.getToken());
        return ApiResponseEntity.toResponseEntity();
    }
}
