package com.smusoak.restapi.controllers;

import com.smusoak.restapi.services.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class S3Controller {
    private final S3Service s3Service;
    @GetMapping("/download/img/{path}/{fileName}")
    public ResponseEntity<byte[]> downloadImg(@PathVariable String path, @PathVariable String fileName) {
        return s3Service.downloadFile(path + "/" + fileName);
    }

    @GetMapping("/download/img/{path1}/{path2}/{fileName}")
    public ResponseEntity<byte[]> downloadImgWithDir(@PathVariable String path1, @PathVariable String path2,
                                                     @PathVariable String fileName) {
        return s3Service.downloadFile(path1 + "/" + path2 + "/" + fileName);
    }
}
