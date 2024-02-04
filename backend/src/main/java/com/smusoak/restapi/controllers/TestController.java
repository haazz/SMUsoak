package com.smusoak.restapi.controllers;

import com.smusoak.restapi.dto.TestDto;
import com.smusoak.restapi.services.RedisService;
import com.smusoak.restapi.services.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userEndPoint() { return "ONLY user can see this"; }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndPoint() { return "ONLY admin can see this"; }

    @PostMapping("/addRedisData")
    @PreAuthorize("hasRole('ADMIN')")
    public String addRedisData(@RequestBody TestDto request) {
        testService.addRedisData(request);
        return "Add Success?";
    }

    @GetMapping("/deleteRedisData")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteRedisData(@RequestBody TestDto request) {
        testService.deleteRedisData(request);
        return "Delete Success?";
    }
}
