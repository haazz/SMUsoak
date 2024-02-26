package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/updateUserDetails")
    public ResponseEntity<ApiResponseEntity> updateUserDetails(@RequestBody UserDto.updateUserDetailsDto request) {
        return userService.updateUserDetails(request);
    }
}

