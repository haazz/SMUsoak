package com.smusoak.restapi.controllers;

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
    public ResponseEntity<ApiResponseEntity> sendAuthCode(@Valid @RequestBody UserDto.sendAuthCodeDto request) throws Exception{
        return authenticationService.sendCodeToMail(request);
    }

    @PostMapping("/mail/verification")
    public ResponseEntity<ApiResponseEntity> mailVerification(@Valid @RequestBody UserDto.mailVerificationDto request) throws Exception{
        return authenticationService.verifiedCode(request);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseEntity> createUser(@RequestBody @Valid UserDto.createUserDto request) throws Exception{
        return authenticationService.createUser(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponseEntity> signin(@Valid @RequestBody UserDto.signinDto request) throws Exception {
        return authenticationService.signin(request);
    }
}
