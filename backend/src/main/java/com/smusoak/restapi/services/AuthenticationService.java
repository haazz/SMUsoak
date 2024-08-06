package com.smusoak.restapi.services;

import com.smusoak.restapi.dto.JwtTokenDto;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.models.Role;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.models.UserDetail;
import com.smusoak.restapi.repositories.UserDetailRepository;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final MailService mailService;
    private static final int AUTH_CODE_INDEX = 0;
    private static final int AUTH_CODE_LENGTH = 6;

    @Value("${spring.mail.auth-code-expirationms}")
    private long authCodeExpirationMillis;

    public JwtTokenDto.JwtAuthenticationResponse signin(UserDto.SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMail(), request.getPassword()));
        User user = userRepository.findByMail(request.getMail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // FCM token 업데이트
        if (request.getFcmToken() != null && !user.getFcmToken().equals(request.getFcmToken())) {
            user.setFcmToken(request.getFcmToken());
            userRepository.save(user);
        }
        // JWT Token 생성
        return jwtService.generateToken(user);
    }

    @Transactional
    public JwtTokenDto.JwtAuthenticationResponse signup(UserDto.SignupRequest request) {
        validateSignup(request);
        UserDetail userDetail = createUserDetail(request);
        User user = createUser(request, userDetail);
        redisService.deleteByKey(request.getMail());
        // JWT Token 생성
        return jwtService.generateToken(user);
    }

    // 메일 인증 코드 전송
    @Transactional
    public void sendCodeToMail(UserDto.SendCodeRequest request) throws MessagingException {
        if (!request.getMail().endsWith("@sangmyung.kr")) {
            throw new CustomException(ErrorCode.WRONG_MAIL_ADDRESS);
        }
        checkDuplicatiedMail(request.getMail());
        String title = "SMUsoak 이메일 인증 번호";
        String authCode = createCode();
        String htmlContent = "<h1>SMUsoak 메일인증</h1>" +
                "<br>SMUsoak에 오신것을 환영합니다!" +
                "<br>아래 [인증 번호]를 앱으로 돌아가 입력해주세요." +
                "<br><h1>" + authCode + "</h1>";
        redisService.deleteByKey(request.getMail());
        redisService.setListOps(request.getMail(), authCode);
        redisService.setExpire(request.getMail(), authCodeExpirationMillis);
        mailService.sendMail(request.getMail(), title, htmlContent);
    }

    // 메일 인증 코드 검증
    @Transactional
    public void verifiedCode(UserDto.MailVerificationRequest request) {
        checkDuplicatiedMail(request.getMail());
        String redisAuthCode = redisService.getListOpsByIndex(request.getMail(), AUTH_CODE_INDEX);

        if (redisAuthCode == null || redisAuthCode.isEmpty()) {
            throw new CustomException(ErrorCode.REDIS_DATA_NOT_FOUND);
        } else if (!redisAuthCode.equals(request.getAuthCode()) && !redisAuthCode.equals("true")) {
            throw new CustomException(ErrorCode.WRONG_AUTH_CODE);
        }
        redisService.deleteByKey(request.getMail());
        redisService.setListOps(request.getMail(), "true");
        redisService.setExpire(request.getMail(), authCodeExpirationMillis);
    }

    public JwtTokenDto.JwtAuthenticationResponse refreshToken(String refreshToken) {
        String username = jwtService.extractIssuer(refreshToken);
        String storedRefreshToken = redisService.getValues("/refreshToken/" + username);
        System.out.println("username: " + username + " refreshToken: " + storedRefreshToken);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.JWT_TOKEN_INVALID);
        }
        return jwtService.generateToken(username);
    }

    private void validateSignup(UserDto.SignupRequest request) {
        // verifiedCode를 거치면 "true"가 redis에 저장되어 있어야 함
        String auth = redisService.getListOpsByIndex(request.getMail(), AUTH_CODE_INDEX);
        if (Objects.isNull(auth) || auth.isEmpty()) {
            throw new CustomException(ErrorCode.REDIS_DATA_NOT_FOUND);
        } else if (!auth.equals("true")) {
            throw new CustomException(ErrorCode.WRONG_AUTH_CODE);
        }
        // 패스워드 규칙, 메일 중복, 닉네임 중복 검사
        checkPasswordRule(request.getPassword());
        checkDuplicatiedMail(request.getMail());
        checkDuplicatiedNickname(request.getNickname());
    }

    private UserDetail createUserDetail(UserDto.SignupRequest request) {
        return userDetailRepository.save(UserDetail.builder()
                .nickname(request.getNickname())
                .gender(request.getGender())
                .age(request.getAge())
                .mbti(request.getMbti())
                .build());
    }

    private User createUser(UserDto.SignupRequest request, UserDetail userDetail) {
        return userRepository.save(User.builder()
                .mail(request.getMail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .userDetail(userDetail)
                .build());
    }

    // 메일 인증 코드 생성
    private String createCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < AUTH_CODE_LENGTH; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("userService.createCode() exception occur");
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }

    private void checkDuplicatiedMail(String mail) {
        userRepository.findByMail(mail)
                .ifPresent(user -> {
                    log.debug("UserService.checkDuplicatedMail exception occur mail: " + mail);
                    throw new CustomException(ErrorCode.USER_MAIL_DUPLICATE);
                });
    }

    private void checkDuplicatiedNickname(String nickname) {
        userDetailRepository.findByNickname(nickname)
                .ifPresent((userDetail) -> {
                    log.debug("UserService.checkDuplicatedNickname exception occur nickname: " + nickname);
                    throw new CustomException(ErrorCode.USER_NICKNAME_DUPLICATE);
                });
    }

    private void checkPasswordRule(String password) {
        //정규표현식 숫자최소1개, 영문 최소1개, 길이 8~20자
        String regExp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$";

        if (!password.matches(regExp)) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD_RULE);
        }
    }
}
