package com.smusoak.restapi.services;

import com.smusoak.restapi.dto.JwtAuthenticationResponse;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.models.Role;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final MailService mailService;
    private static final int AUTH_CODE_INDEX = 0;

    @Value("${spring.mail.auth-code-expirationms}")
    private long authCodeExpirationMillis;

    public ResponseEntity<ApiResponseEntity> signin(UserDto.signinDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getMail(), request.getPassword()));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.WRONG_MAIL_OR_PASSWORD);
        }
        var user = userRepository.findByMail(request.getMail())
                .orElseThrow(() -> new CustomException(ErrorCode.WRONG_MAIL_OR_PASSWORD));
        // JWT Token 생성
        var jwt = jwtService.generateToken(user);
        return ApiResponseEntity.toResponseEntity(
                JwtAuthenticationResponse.builder().token(jwt).build());
    }

    public ResponseEntity<ApiResponseEntity> createUser(UserDto.createUserDto request) {
        User user = new User();
        String auth = redisService.getListOpsByIndex(request.getMail(), AUTH_CODE_INDEX);
        if (auth == null || auth.isEmpty()) {
            throw new CustomException(ErrorCode.REDIS_DATA_NOT_FOUND);
        }
        // verifiedCode를 거치면 "true"가 redis에 저장되어 있어야 함
        else if (!auth.equals("true")) {
            throw new CustomException(ErrorCode.WRONG_AUTH_CODE);
        }
        this.checkDuplicatiedMail(request.getMail());
        this.checkPasswordRule(request.getPassword());

        // 유저 DB에 저장
        user.setMail(request.getMail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMailAuth(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.ROLE_USER);
        user.setAge(request.getAge());
        user.setGender(Character.toUpperCase(request.getGender()));
        user.setMajor(request.getMajor());
        this.userRepository.save(user);
        redisService.deleteByKey(request.getMail());
        // JWT Token 생성
        var jwt = jwtService.generateToken(user);
        return ApiResponseEntity.toResponseEntity(
                JwtAuthenticationResponse.builder().token(jwt).build());
    }

    public ResponseEntity<ApiResponseEntity> sendCodeToMail(UserDto.sendAuthCodeDto request) throws MessagingException {
        String toMail = request.getMail();
        if (!toMail.endsWith("@sangmyung.kr")) {
            throw new CustomException(ErrorCode.WRONG_MAIL_ADDRESS);
        }
        this.checkDuplicatiedMail(toMail);
        String title = "SMUsoak 이메일 인증 번호";
        String authCode = this.createCode();
        String htmlContent = "<h1>SMUsoak 메일인증</h1>" +
                "<br>SMUsoak에 오신것을 환영합니다!" +
                "<br>아래 [인증 번호]를 앱으로 돌아가 입력해주세요." +
                "<br><h1>" + authCode + "</h1>";
        redisService.deleteByKey(toMail);
        redisService.setListOps(toMail, authCode);
        redisService.setExpire(toMail, authCodeExpirationMillis);
        mailService.sendMail(toMail, title, htmlContent);
        return ApiResponseEntity.toResponseEntity();
    }

    public ResponseEntity<ApiResponseEntity> verifiedCode(UserDto.mailVerificationDto request) {
        this.checkDuplicatiedMail(request.getMail());
        String redisAuthCode = redisService.getListOpsByIndex(request.getMail(), AUTH_CODE_INDEX);

        if(redisAuthCode == null || redisAuthCode.isEmpty()) {
            throw new CustomException(ErrorCode.REDIS_DATA_NOT_FOUND);
        }
        else if(!redisAuthCode.equals(request.getAuthCode()) && !redisAuthCode.equals("true")) {
            throw new CustomException(ErrorCode.WRONG_AUTH_CODE);
        }
        redisService.deleteByKey(request.getMail());
        redisService.setListOps(request.getMail(), "true");
        redisService.setExpire(request.getMail(), authCodeExpirationMillis);
        return ApiResponseEntity.toResponseEntity();
    }

    private String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("userService.createCode() exception occur");
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }

    private void checkDuplicatiedMail(String mail) {
        Optional<User> users = userRepository.findByMail(mail);
        if (users.isPresent()) {
            log.debug("UserService.checkDuplicatedMail exception occur mail: " + mail);
            throw new CustomException(ErrorCode.USER_MAIL_DUPLICATE);
        }
    }

    private void checkPasswordRule(String password) {
        //정규표현식 숫자최소1개,대소문자 최소1개, 길이 8~20자
        String regExp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$";

        if (!password.matches(regExp)) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD_RULE);
        }
    }
}
