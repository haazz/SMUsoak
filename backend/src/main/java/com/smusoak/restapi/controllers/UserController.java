package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.UserCreateDto;
import com.smusoak.restapi.dto.UserDetailsDto;
import com.smusoak.restapi.response.ApiResponseEntity;

import com.smusoak.restapi.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;


	@GetMapping("/getAllUsers")
	public ResponseEntity<ApiResponseEntity> getAllUsers(HttpServletRequest request) {
		return userService.getAllUser();
	}


	@PostMapping("/signup")
	public ResponseEntity<ApiResponseEntity> sendAuthCode(@RequestBody UserCreateDto userCreateDto) throws Exception {
		return userService.sendCodeToMail(userCreateDto);
	}
	@GetMapping("/mailVerification")
	public String mailVerification(@RequestParam("mail") String mail, @RequestParam("authCode") String authCode) {
		// HTML 추가 필요
		if (userService.verifiedCode(mail, authCode)) {
			userService.createUser(mail);
			return "이메일 인증에 성공했습니다! \n앱으로 돌아가 로그인 해주세요.\n";
		}
		return "이메일 인증을 재시도 해주세요!";
	}

	@PostMapping("/signin")
	public ResponseEntity<ApiResponseEntity> signin(@RequestBody UserCreateDto userCreateDto) {
		return userService.signin(userCreateDto);
	}

	@PostMapping("/updateUserDetails")
	public ResponseEntity<ApiResponseEntity> updateUserDetails(@RequestBody UserDetailsDto userDetailsDto) {
		return userService.updateUserDetails(userDetailsDto);
	}
}
