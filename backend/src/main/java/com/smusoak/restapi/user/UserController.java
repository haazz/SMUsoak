package com.smusoak.restapi.user;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

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
	public UserCreateDto testRestApitPost(@RequestBody UserCreateDto userCreateDto) {
		try {
			userService.create(userCreateDto);
		} catch(DataIntegrityViolationException e) {
			e.getMessage();
		} catch (Exception e) {
			e.getMessage();
		}
		
		return userCreateDto;
	}

	
}
