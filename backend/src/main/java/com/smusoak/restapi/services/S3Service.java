package com.smusoak.restapi.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.smusoak.restapi.dto.ImgDto;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.coyote.Response;
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
import java.io.InputStream;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void updateImg(String title, MultipartFile file) {
        System.out.println("services.S3Service.updateImg 파일 이름:" + title);
        MultipartFile multipartFile = resizeImg(file);
        this.updateS3Img(title, multipartFile, file.getContentType());
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
            if(contentType.endsWith("jpeg") || contentType.endsWith("jpg")) {
                type = "jpeg";
            }
            else if(contentType.endsWith("png")) {
                type = "png";
            }
            else {
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

    public void updateS3Img(String key, MultipartFile file, String contentType) {
        try {
            System.out.println(key);
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(file.getSize());
            amazonS3Client.deleteObject(bucket, key);
            amazonS3Client.putObject(bucket, key, file.getInputStream(),metadata);
        }
        catch (Exception e) {
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
