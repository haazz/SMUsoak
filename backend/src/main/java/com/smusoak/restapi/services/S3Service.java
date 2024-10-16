package com.smusoak.restapi.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void updateImg(String title, MultipartFile file) {
        log.info("services.S3Service.updateImg 파일 이름:" + title);
        MultipartFile multipartFile = resizeImg(file);
//        MultipartFile multipartFile = file;
        this.updateS3Img(title, multipartFile, file.getContentType());
    }

    public void updateS3Img(String key, MultipartFile file, String contentType) {
        try {
            System.out.println(key);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(file.getSize());
            amazonS3Client.deleteObject(bucket, key);
            amazonS3Client.putObject(bucket, key, file.getInputStream(), metadata);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
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
            if (o == null) {
                throw new CustomException(ErrorCode.S3_DATA_NOT_FOUND);
            }
            S3ObjectInputStream objectInputStream = ((S3Object) o).getObjectContent();
            byte[] bytes = IOUtils.toByteArray(objectInputStream);
            String file = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            HttpHeaders httpHeaders = new HttpHeaders();
            // png, jpg, jpeg 확장자 분리
            String contentType = o.getObjectMetadata().getContentType().toString();
            if (contentType.endsWith("jpeg") || contentType.endsWith("jpg")) {
                httpHeaders.setContentType(MediaType.parseMediaType("image/jpeg"));
            } else if (contentType.endsWith("png")) {
                httpHeaders.setContentType(MediaType.parseMediaType("image/png"));
            } else {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
            httpHeaders.setContentLength(bytes.length);
            httpHeaders.setContentDispositionFormData("attachment", file);
            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }

    public String createPresignedGetUrl(String fileName) {
        try {
            // Set the presigned URL to expire after one hour.
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL.
            log.info("Generating pre-signed URL.");
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket,
                    fileName)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);
            URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return url.toString();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            log.error(e.getStackTrace().toString());
            throw new CustomException(ErrorCode.S3_DATA_NOT_FOUND);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            log.error(e.getStackTrace().toString());
            throw new CustomException(ErrorCode.S3_DATA_NOT_FOUND);
        }
    }

    public String createPresignedPutUrl(String fileName) {
        // Set the pre-signed URL to expire after 12 hours.
        java.util.Date expiration = new java.util.Date();
        long expirationInMs = expiration.getTime();
        expirationInMs += 1000 * 60 * 60 * 12;
        expiration.setTime(expirationInMs);

        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket,
                    fileName)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(expiration);
            URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
            // print URL
            log.info("\n\rGenerated URL: " + url.toString());
            // Print curl command to consume URL
            log.info("\n\rExample command to use URL for file upload: \n\r");
            log.info("curl --request PUT --upload-file /path/to/" + fileName + " '" + url.toString()
                    + "' -# > /dev/null");
            return url.toString();
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            throw new CustomException(ErrorCode.S3_DATA_NOT_FOUND);
        }
    }

    private MultipartFile resizeImg(MultipartFile multipartFile) {
        try {
            BufferedImage bufferedImage = Thumbnails.of(multipartFile.getInputStream())
                    .size(300, 300)
                    .asBufferedImage();

            // 기존 file type 저장
            String contentType = multipartFile.getContentType().toString();
            String type;
            // jpeg, jpg, png가 아닌 경우 throw
            if (contentType.endsWith("jpeg") || contentType.endsWith("jpg")) {
                type = "jpeg";
            } else if (contentType.endsWith("png")) {
                type = "png";
            } else {
                System.out.println("UserService/resizeImg: not png and jpeg");
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            // "Buffered Image" -> "byte array" -> MultipartFile
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, type, baos);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
            // MockMultipartFile은 원래 test용으로 만들어졌지만 multipartfile로 변환할때도 사용한다.
            return new MockMultipartFile("fileName", byteArrayInputStream.readAllBytes());

        } catch (Exception e) {
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }
}
