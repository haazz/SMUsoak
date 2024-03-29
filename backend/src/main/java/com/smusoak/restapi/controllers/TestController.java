package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.TestDto;
import com.smusoak.restapi.response.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    @GetMapping()
    public ResponseEntity<ApiResponseEntity> test() {
        return ApiResponseEntity.toResponseEntity(
                TestDto.responseAuth.builder().message("Test").build());
    }

    @GetMapping("/hello")
    public ResponseEntity<ApiResponseEntity> hello() {
        return ApiResponseEntity.toResponseEntity(
                TestDto.responseAuth.builder().message("Hello!").build());
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseEntity> userEndPoint() {
        return ApiResponseEntity.toResponseEntity(
                TestDto.responseAuth.builder().message("ONLY user can see this").build());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseEntity> adminEndPoint() {
        return ApiResponseEntity.toResponseEntity(
                TestDto.responseAuth.builder().message("ONLY admin can see this").build());
    }
}
