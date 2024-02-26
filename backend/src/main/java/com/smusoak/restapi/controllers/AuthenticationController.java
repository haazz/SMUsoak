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

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseEntity> sendAuthCode(@RequestBody UserDto.sendAuthCodeDto request) throws Exception{
        return authenticationService.sendCodeToMail(request);
    }

    @GetMapping("/mailVerification")
    public String mailVerification(@RequestBody UserDto.mailVerificationDto request) {
        // HTML 추가 필요
        if (authenticationService.verifiedCode(request)) {
            return "이메일 인증에 성공했습니다! \n앱으로 돌아가 로그인 해주세요.\n";
        }
        return "이메일 인증을 재시도 해주세요!";
    }
    @PostMapping("/signin")
    public ResponseEntity<ApiResponseEntity> signin(@RequestBody UserDto.signinDto request) {
        return authenticationService.signin(request);
    }
}

//@RestController
//@RequestMapping("/authentication")
//@RequiredArgsConstructor
//public class AuthenticationController {
//
//    private final AuthenticationService authenticationService;
//
//    @PostMapping("/sendAuthCode")
//    public ResponseEntity<ApiResponseEntity> sendAuthCode(@RequestBody UserDto.sendAuthCodeDto request) throws Exception{
//        return authenticationService.sendCodeToMail(request);
//    }
//
//    @GetMapping("/mailVerification")
//    public ResponseEntity<ApiResponseEntity> mailVerification(@RequestBody UserDto.mailVerificationDto request) throws Exception{
//        return authenticationService.verifiedCode(request);
//    }
//
//    @PostMapping("/createUser")
//    public ResponseEntity<ApiResponseEntity> createUser(@RequestBody UserDto.createUserDto request) throws Exception{
//        authenticationService.createUser(request);
//    }
//
//    @PostMapping("/signin")
//    public ResponseEntity<ApiResponseEntity> signin(@RequestBody UserDto.signinDto request) {
//        return authenticationService.signin(request);
//    }
//}
