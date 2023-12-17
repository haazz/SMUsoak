package com.smusoak.restapi.user;

import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
	@GetMapping("/test")
	public HashMap<String, Object> testRestApi() {
		HashMap<String, Object> test = new HashMap<>();
		test.put("abcaa", "ddeeed");
		return test;
	}
	@PostMapping("/test")
	public Users testRestApitPost(Users users) {
		return users;
	}
	
}
