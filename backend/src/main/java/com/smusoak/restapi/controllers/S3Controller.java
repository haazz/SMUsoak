package com.smusoak.restapi.controllers;

import com.smusoak.restapi.services.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;

    @GetMapping("/download/img/{fileName}")
    public ResponseEntity<byte[]> downloadImg(@PathVariable String fileName) {
        return s3Service.downloadFile(fileName);
    }

}
