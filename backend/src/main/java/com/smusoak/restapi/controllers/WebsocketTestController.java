package com.smusoak.restapi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebsocketTestController {
    @GetMapping("/api/v1/test/websocket")
    public String websocketTest() {
        return "websocketTest";
    }
}
