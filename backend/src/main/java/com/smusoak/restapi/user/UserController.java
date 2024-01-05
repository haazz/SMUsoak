package com.smusoak.restapi.user;

import java.util.HashMap;
import java.util.List;

import com.smusoak.restapi.BusinessLogicException;
import com.smusoak.restapi.ExceptionCode;
import com.smusoak.restapi.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
	private final RedisService redisService;

	@GetMapping("/allUsers")
	public List<Users> allUsers() {
		return userService.getAllUser();
	}

	@GetMapping("/redisAllUsers")
	public String redisAllUsers(@RequestBody UserCreateDto userCreateDto) {
		return redisService.getValues(userCreateDto.getMail());
	}
	@PostMapping("/sendAuthCode")
	public String sendAuthCode(@RequestBody UserCreateDto userCreateDto) {
		try {
			userService.sendCodeToMail(userCreateDto);
			return "Finally I'm win";
		} catch(DataIntegrityViolationException e) {
			log.debug("UserController.TestRestApiPost exception occur mail: " +
					userCreateDto.getMail() + e.getMessage());
			return e.getMessage();
		} catch (Exception e) {
			log.debug("UserController.TestRestApiPost exception occur mail: " +
					userCreateDto.getMail() + e.getMessage());
			throw new BusinessLogicException(ExceptionCode.USER_MAIL_DUPLICATE);
		}

		//return "I can do this all day!";
	}

	@GetMapping("/mailVerification")
	public String mailVerification(@RequestParam("mail") String mail, @RequestParam("authCode") String authCode) {
		boolean verificationResult = userService.verifiedCode(mail, authCode);
		if (verificationResult && userService.createUser(mail)) {
			return "축하합니다! 회원가입을 완료했습니다!";
		}
		return "이메일 인증을 재시도 해주세요!";
	}
	
}
