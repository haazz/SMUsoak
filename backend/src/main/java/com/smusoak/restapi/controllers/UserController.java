package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.ImgDto;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/update/info")
    public ResponseEntity<ApiResponseEntity> updateUserDetails(@RequestBody UserDto.UpdateUserDetailsRequest request) {
        userService.updateUserDetails(request);
        return ApiResponseEntity.toResponseEntity();
    }

    @PostMapping(value = "/update/img", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponseEntity> updateUserImg(@RequestPart(value="info", required=true) ImgDto.UpdateUserImgRequest request,
                                                           @RequestPart(value="file", required=true) MultipartFile file) {
        request.setFile(file);
        userService.updateUserImg(request);
        return ApiResponseEntity.toResponseEntity();
    }

    @PostMapping("/info")
    public ResponseEntity<ApiResponseEntity> getUserIfos(@RequestBody UserDto.UserInfoRequest request) {
        List<UserDto.UserInfoResponse> userImageResponses =  userService.getUserInfo(request);
        return ApiResponseEntity.toResponseEntity(userImageResponses);
    }

    @GetMapping("/check/nickname/{nickname}")
    public ResponseEntity<ApiResponseEntity> checkDuplicatedNickname(@PathVariable String nickname) {
        boolean checkDuplicated = userService.checkDuplicatiedNickname(nickname);
        return ApiResponseEntity.toResponseEntity(UserDto.DuplicatedNicknameResponse.builder()
                .available(checkDuplicated)
                .build());
    }
}

