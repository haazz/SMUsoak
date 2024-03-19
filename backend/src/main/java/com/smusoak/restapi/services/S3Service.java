package com.smusoak.restapi.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ResponseEntity<ApiResponseEntity> updateS3Img(UserDto.updateUserImg request) {
        try {
            System.out.println(request.getMail());
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(request.getFile().getContentType());
            metadata.setContentLength(request.getFile().getSize());
            amazonS3Client.deleteObject(bucket, request.getMail());
            amazonS3Client.putObject(bucket, request.getMail(), request.getFile().getInputStream(),metadata);
        }
        catch (Exception e) {
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
        return ApiResponseEntity.toResponseEntity();
    }

    public S3Object getObject(String key) {
        try {
            S3Object o = amazonS3Client.getObject(bucket, key);
            return o;
        } catch (Exception e) {
            return null;
        }
    }

    public ResponseEntity<byte[]> downloadFile(String fileName) {
        try {
            S3Object o = this.getObject(fileName);
            if(o == null) {
                throw new CustomException(ErrorCode.S3_DATA_NOT_FOUND);
            }
            S3ObjectInputStream objectInputStream = ((S3Object) o).getObjectContent();
            byte[] bytes = IOUtils.toByteArray(objectInputStream);
            String file = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            HttpHeaders httpHeaders = new HttpHeaders();
            // png, jpg, jpeg 확장자 분리
            String contentType = o.getObjectMetadata().getContentType().toString();
            if(contentType.endsWith("jpeg") || contentType.endsWith("jpg")) {
                httpHeaders.setContentType(MediaType.parseMediaType("image/jpeg"));
            }
            else if(contentType.endsWith("png")) {
                httpHeaders.setContentType(MediaType.parseMediaType("image/png"));
            }
            else {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
            httpHeaders.setContentLength(bytes.length);
            httpHeaders.setContentDispositionFormData("attachment", file);
            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }
}
