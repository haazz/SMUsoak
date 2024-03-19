package com.smusoak.restapi.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
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

    public ResponseEntity<ApiResponseEntity> updateUserDetails(UserDto.updateUserDetailsDto request) {
        Optional<User> users = userRepository.findByMail(request.getMail());
        if (users.isPresent()) {
            users.get().setAge(request.getAge());
            users.get().setGender(request.getGender());
            this.userRepository.save(users.get());
            return ApiResponseEntity.toResponseEntity();
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    public ResponseEntity<ApiResponseEntity> updateUserImg(UserDto.updateUserImg request) {
        System.out.println(request.getMail());
        return s3Service.updateS3Img(request);
    }

    public ResponseEntity<ApiResponseEntity> getUserImg(UserDto.getUserImg request) {
        List<UserDto.userImageResponse> userImageResponses = new ArrayList<>();
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
            userImageResponses.add(UserDto.userImageResponse
                                    .builder()
                                    .mail(mail)
                                    .url(downloadUrl + mail)
                                    .type(type)
                                    .build());
        }
        return ApiResponseEntity.toResponseEntity(userImageResponses);
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
