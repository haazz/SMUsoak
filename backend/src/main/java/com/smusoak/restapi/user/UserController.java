package com.smusoak.restapi.user;

import java.util.HashMap;
import java.util.List;

import com.smusoak.restapi.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		return redisService.getValues(userCreateDto.getStudentid());
	}
	@PostMapping("/test")
	public String testRestApiPost(@RequestBody UserCreateDto userCreateDto) {
		try {
			userService.sendCodeToMail(userCreateDto.getStudentid());
			// userService.create(userCreateDto);
			// return new ResponseEntity<>(HttpStatus.OK);
			return "Finally I'm win";
		} catch(DataIntegrityViolationException e) {
			log.debug("UserController.TestRestApiPost exception occur studentid: " +
					userCreateDto.getStudentid() + e.getMessage());
		} catch (Exception e) {
			log.debug("UserController.TestRestApiPost exception occur studentid: " +
					userCreateDto.getStudentid() + e.getMessage());
		}

		return "I can do this all day!";
	}

	
}
