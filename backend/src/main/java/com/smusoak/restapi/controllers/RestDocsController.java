package com.smusoak.restapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Rest docs를 보여주기 위한 controller
// 이렇게 만들지 않으면 Security에서 permitAll이 작동하지 않는다.
@Controller
public class RestDocsController {
    @GetMapping
    public String hello() {
        return "index";
    }
}
