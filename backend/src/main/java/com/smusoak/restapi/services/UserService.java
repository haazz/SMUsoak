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

    public void updateUserImg(ImgDto.UpdateUserImgRequest request) {
        System.out.println(request.getMail());
        System.out.println(request.getFile().getSize());
        MultipartFile multipartFile = resizeImg(request.getFile());
        s3Service.updateS3Img(request.getMail(), multipartFile, request.getFile().getContentType());
    }

    public List<UserDto.UserInfoResponse> getUserInfo(UserDto.UserInfoRequest request) {
        List<UserDto.UserInfoResponse> userInfoResponses = new ArrayList<>();
        for(String mail : request.getMailList()) {
            // UserDetail 가져오기
            Optional<UserDetail> userDetail = userDetailRepository.findByUserMail(mail);
            if(!userDetail.isPresent()) {
                continue;
            }
            S3Object o = s3Service.getObject(mail);
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
                    .imgUrl(downloadUrl + mail)
                    .imgType(type)
                    .build());
        }
        return userInfoResponses;
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
