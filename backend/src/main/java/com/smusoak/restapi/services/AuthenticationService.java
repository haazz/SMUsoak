package com.smusoak.restapi.services;

import com.smusoak.restapi.dto.JwtAuthenticationResponse;
import com.smusoak.restapi.dto.SignInRequest;
import com.smusoak.restapi.dto.SignUpRequest;
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
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final MailService mailService;

    private static final int AUTH_CODE_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    @Value("${spring.mail.auth-code-expirationms}")
    private long authCodeExpirationMillis;

    public ResponseEntity<ApiResponseEntity> signin(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMail(), request.getPassword()));
        var user = userRepository.findByMail(request.getMail())
                .orElseThrow(() -> new CustomException(ErrorCode.WRONG_MAIL_OR_PASSWORD));
        var jwt = jwtService.generateToken(user);
        return ApiResponseEntity.toResponseEntity(
                JwtAuthenticationResponse.builder().token(jwt).build());
    }

    public void createUser(String mail) {
        User user = new User();
        String password = redisService.getListOpsByIndex(mail, PASSWORD_INDEX);
        if (password.isEmpty()) {
            throw new CustomException(ErrorCode.REDIS_DATA_NOT_FOUND);
        }
        user.setMail(mail);
        user.setPassword(passwordEncoder.encode(password));
        user.setMailAuth(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.ROLE_USER);
        this.userRepository.save(user);
        redisService.deleteByKey(mail);
    }

    public ResponseEntity<ApiResponseEntity> sendCodeToMail(SignUpRequest request) throws MessagingException {
        String toMail = request.getMail();
        if (!toMail.endsWith("@sangmyung.kr")) {
            throw new CustomException(ErrorCode.WRONG_MAIL_ADDRESS);
        }
        this.checkDuplicatiedMail(toMail);
        String title = "SMUsoak 이메일 인증 번호";
        String authCode = this.createCode();
        String htmlContent = "<h1>SMUsoak 메일인증</h1>" +
                "<br>SMUsoak에 오신것을 환영합니다!" +
                "<br>아래 [이메일 인증 확인]을 눌러주세요." +
                "<br><a href='http://localhost:8080/authentication/mailVerification?mail=" +
                toMail + "&authCode=" + authCode +
                "' target='_blank'>이메일 인증 확인</a>";
        mailService.sendMail(toMail, title, htmlContent);
        redisService.deleteByKey(toMail);
        redisService.setListOps(toMail, authCode, request.getPassword());
        redisService.setExpire(toMail, authCodeExpirationMillis);
        return ApiResponseEntity.toResponseEntity();
    }

    public boolean verifiedCode(String mail, String authCode) {
        this.checkDuplicatiedMail(mail);
        String redisAuthCode = redisService.getListOpsByIndex(mail, AUTH_CODE_INDEX);

        if(redisAuthCode.isEmpty()) {
            throw new CustomException(ErrorCode.REDIS_DATA_NOT_FOUND);
        }
        boolean authResult = redisAuthCode.equals(authCode);
        return authResult;
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
}
