package com.smusoak.restapi.user;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.smusoak.restapi.redis.RedisService;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
	public ResponseEntity<ApiResponseEntity> getAllUsers() {
		return userService.getAllUser();
	}

	@PostMapping("/sendAuthCode")
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

	@PostMapping("/updateUserDetails")
	public ResponseEntity<ApiResponseEntity> updateUserDetails(@RequestBody UserDetailsDto userDetailsDto) {
		return userService.updateUserDetails(userDetailsDto);
	}
}
