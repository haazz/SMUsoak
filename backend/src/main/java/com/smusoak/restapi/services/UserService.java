package com.smusoak.restapi.services;

import com.amazonaws.services.s3.model.S3Object;
import com.smusoak.restapi.dto.ImgDto;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.models.UserDetail;
import com.smusoak.restapi.repositories.UserDetailRepository;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.url}")
    private String downloadUrl;

    public void updateUserDetails(UserDto.UpdateUserDetailsRequest request) {
        Optional<UserDetail> userDetail = userDetailRepository.findByUserMail(request.getMail());
        if (!userDetail.isPresent()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        if (request.getNickname() != null && !userDetail.get().getNickname().equals(request.getNickname())) {
            if (checkDuplicatiedNickname(request.getNickname())) {
                userDetail.get().setNickname(request.getNickname());
            }
        }
        userDetail.get().setAge(request.getAge());
        userDetail.get().setGender(request.getGender());
        userDetail.get().setMbti(request.getMbti());

        userDetailRepository.save(userDetail.get());
    }

    // 닉네임 중복시 return false;
    public boolean checkDuplicatiedNickname(String nickname) {
        Optional<UserDetail> userDetail = userDetailRepository.findByNickname(nickname);
        if (userDetail.isPresent()) {
            return false;
        }
        return true;
    }

    public List<UserDto.UserInfoResponse> getUserInfo(UserDto.UserInfoRequest request) {
        List<UserDto.UserInfoResponse> userInfoResponses = new ArrayList<>();
        for(String mail : request.getMailList()) {
            // UserDetail 가져오기
            Optional<UserDetail> userDetail = userDetailRepository.findByUserMail(mail);
            if(!userDetail.isPresent()) {
                continue;
            }
            S3Object o = s3Service.getObject("user/" + mail);
            if(o == null) {
                userInfoResponses.add(UserDto.UserInfoResponse.builder()
                        .mail(mail)
                        .nickname(userDetail.get().getNickname())
                        .age(userDetail.get().getAge())
                        .gender(userDetail.get().getGender())
                        .mbti(userDetail.get().getMbti())
                        .build());
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
            userInfoResponses.add(UserDto.UserInfoResponse.builder()
                    .mail(mail)
                    .nickname(userDetail.get().getNickname())
                    .age(userDetail.get().getAge())
                    .gender(userDetail.get().getGender())
                    .mbti(userDetail.get().getMbti())
                    .imgUrl(s3Service.createPresignedGetUrl("user/" + mail))
                    .imgType(type)
                    .imgUpdateDate(String.valueOf(userDetail.get().getImgUpdateDate()))
                    .build());
            try {
                o.close();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        return userInfoResponses;
    }

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByMail(username).orElseThrow(() -> new UsernameNotFoundException("Usernam not found"));
            }
        };
    }

    public void updateImgDate(String mail) {
        Optional<UserDetail> userDetail = userDetailRepository.findByUserMail(mail);
        if (userDetail.isPresent()) {
            userDetail.get().setImgUpdateDate(LocalDateTime.now());
            userDetailRepository.save(userDetail.get());
        }
    }
}
