package com.smusoak.restapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@GetMapping("/hello")
	@ResponseBody
	public ResponseEntity<String> hello() {
		return ResponseEntity.ok()
				.body("Hello World!");
	}
}
