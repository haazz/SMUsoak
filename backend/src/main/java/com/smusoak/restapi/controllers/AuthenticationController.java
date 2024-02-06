package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.SignInRequest;
import com.smusoak.restapi.dto.SignUpRequest;
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
    public ResponseEntity<ApiResponseEntity> sendAuthCode(@RequestBody SignUpRequest request) throws Exception{
        return authenticationService.sendCodeToMail(request);
    }

    @GetMapping("/mailVerification")
    public String mailVerification(@RequestParam("mail") String mail, @RequestParam("authCode") String authCode) {
        // HTML 추가 필요
        if (authenticationService.verifiedCode(mail, authCode)) {
            authenticationService.createUser(mail);
            return "이메일 인증에 성공했습니다! \n앱으로 돌아가 로그인 해주세요.\n";
        }
        return "이메일 인증을 재시도 해주세요!";
    }
    @PostMapping("/signin")
    public ResponseEntity<ApiResponseEntity> signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request);
    }
}
