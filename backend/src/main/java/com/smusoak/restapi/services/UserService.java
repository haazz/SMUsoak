package com.smusoak.restapi.services;

import com.amazonaws.services.s3.model.S3Object;
import com.smusoak.restapi.dto.ImgDto;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.url}")
    private String downloadUrl;
    @Value("${cloud.aws.s3.limit-size}")
    private Integer imgLimitSize;

    public void updateUserDetails(UserDto.UpdateUserDetailsRequest request) {
        Optional<User> users = userRepository.findByMail(request.getMail());
        if (users.isPresent()) {
            users.get().setAge(request.getAge());
            users.get().setGender(request.getGender());
            this.userRepository.save(users.get());
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    public void updateUserImg(ImgDto.UpdateUserImgRequest request) {
        System.out.println(request.getMail());
        System.out.println(request.getFile().getSize());
        MultipartFile multipartFile = resizeImg(request.getFile());
        s3Service.updateS3Img(request.getMail(), multipartFile, request.getFile().getContentType());
    }

    public List<ImgDto.UserImageResponse> getUserImg(ImgDto.UserImgRequest request) {
        List<ImgDto.UserImageResponse> userImageResponses = new ArrayList<>();
        for(String mail : request.getMailList()) {
            S3Object o = s3Service.getObject(mail);
            if(o == null) {
                continue;
            }
            String contentType = o.getObjectMetadata().getContentType().toString();
            String type;
            if(contentType.endsWith("jpeg") || contentType.endsWith("jpg")) {
                type = "image/jpeg";
            }
            else if(contentType.endsWith("png")) {
                type = "image/png";
            }
            else {
                continue;
            }
            userImageResponses.add(ImgDto.UserImageResponse
                                    .builder()
                                    .mail(mail)
                                    .url(downloadUrl + mail)
                                    .type(type)
                                    .build());
        }
        return userImageResponses;
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


    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByMail(username).orElseThrow(() -> new UsernameNotFoundException("Usernam not found"));
            }
        };
    }
}
