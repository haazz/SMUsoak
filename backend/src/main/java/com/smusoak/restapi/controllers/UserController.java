package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/update/info")
    public ResponseEntity<ApiResponseEntity> updateUserDetails(@RequestBody UserDto.updateUserDetailsDto request) {
        return userService.updateUserDetails(request);
    }

    @PostMapping(value = "/update/img", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponseEntity> updateUserImg(@RequestPart(value="info", required=true) UserDto.updateUserImg request,
                                                           @RequestPart(value="file", required=true) MultipartFile file) {
        request.setFile(file);
        return userService.updateUserImg(request);
    }

    @GetMapping(value = "/imgs")
    public ResponseEntity<ApiResponseEntity> getUserImg(@RequestBody UserDto.getUserImg request) {
        return userService.getUserImg(request);
    }
}

