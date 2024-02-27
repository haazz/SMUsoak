package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/sendAuthCode")
    public ResponseEntity<ApiResponseEntity> sendAuthCode(@RequestBody UserDto.sendAuthCodeDto request) throws Exception{
        return authenticationService.sendCodeToMail(request);
    }

    @PostMapping("/mailVerification")
    public ResponseEntity<ApiResponseEntity> mailVerification(@RequestBody UserDto.mailVerificationDto request) throws Exception{
        return authenticationService.verifiedCode(request);
    }

    @PostMapping("/createUser")
    public ResponseEntity<ApiResponseEntity> createUser(@RequestBody UserDto.createUserDto request) throws Exception{
        return authenticationService.createUser(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponseEntity> signin(@RequestBody UserDto.signinDto request) {
        return authenticationService.signin(request);
    }
}
