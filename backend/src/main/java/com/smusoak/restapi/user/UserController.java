package com.smusoak.restapi.user;

import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
	
	@GetMapping("/test")
	public List<Users> testRestApi() {
		
		return userService.getAllUser();
	}
	@PostMapping("/test")
	public UserCreateDto testRestApiPost(@RequestBody UserCreateDto userCreateDto) {
		try {
			userService.create(userCreateDto);
		} catch(DataIntegrityViolationException e) {
			log.debug("UserController.TestRestApiPost exception occur studentid: " +
					userCreateDto.getStudentid() + e.getMessage());
		} catch (Exception e) {
			log.debug("UserController.TestRestApiPost exception occur studentid: " +
					userCreateDto.getStudentid() + e.getMessage());
		}
		
		return userCreateDto;
	}

	
}
