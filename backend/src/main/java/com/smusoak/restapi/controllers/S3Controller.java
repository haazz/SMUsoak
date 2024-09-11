package com.smusoak.restapi.controllers;

import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
