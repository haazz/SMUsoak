package com.smusoak.restapi.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import com.smusoak.restapi.models.MatchingInfo;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import com.smusoak.restapi.services.MatchingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smusoak.restapi.dto.*;
import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/match")
public class MatchingController {
    private final MatchingService matchingService;
    @PostMapping("/onetoone")
    public ResponseEntity<String> matchUsers(@RequestBody MatchingInfoDto.MatchingRequest matchingRequest) {
        try {
            matchingService.matchUsers(matchingRequest);
            return ResponseEntity.ok("Matching completed.");
        } catch (IllegalArgumentException e) {
            log.error("Error during matching:", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Matching failed *: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error during matching:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Matching failed: " + e.getMessage());
        }
    }
}
