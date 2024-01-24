package com.smusoak.restapi.services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.smusoak.restapi.dto.JwtAuthenticationResponse;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import com.smusoak.restapi.dto.UserCreateDto;
import com.smusoak.restapi.dto.UserDetailsDto;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.models.Users;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
	private static final int AUTH_CODE_INDEX = 0;
	private static final int PASSWORD_INDEX = 1;
	private final UserRepository userRepository;
	private final MailService mailService;
	private final RedisService redisService;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	@Value("${spring.mail.auth-code-expiration-millis}")
	private long authCodeExpirationMillis;

	public void createUser(String mail) {
		Users user = new Users();
		String password = redisService.getListOpsByIndex(mail, PASSWORD_INDEX);
		if (password.isEmpty()) {
			throw new CustomException(ErrorCode.REDIS_DATA_NOT_FOUND);
		}
		user.setMail(mail);
		user.setPassword(password);
		user.setMailAuth(true);
		user.setCreatedAt(LocalDateTime.now());
		this.userRepository.save(user);
		redisService.deleteByKey(mail);
	}
	
	public UserDetailsDto getUser(String mail) {
		Optional<Users> user = this.userRepository.findByMail(mail);
		return UserDetailsDto.builder()
				.mail(user.get().getMail())
				.age(user.get().getAge())
				.gender(user.get().getGender())
				.major(user.get().getMajor())
				.build();
	}
	
	public ResponseEntity<ApiResponseEntity> getAllUser() {
		List<Users> users = this.userRepository.findAll();
		return ApiResponseEntity.toResponseEntity(users);
	}


	public ResponseEntity<ApiResponseEntity> sendCodeToMail(UserCreateDto userCreateDto) throws MessagingException {
		String toMail = userCreateDto.getMail();
		this.checkDuplicatiedMail(toMail);
		String title = "SMUsoak 이메일 인증 번호";
		String authCode = this.createCode();
		String htmlContent = "<h1>SMUsoak 메일인증</h1>" +
				"<br>SMUsoak에 오신것을 환영합니다!" +
				"<br>아래 [이메일 인증 확인]을 눌러주세요." +
				"<br><a href='http://localhost:8080/user/mailVerification?mail=" +
				toMail + "&authCode=" + authCode +
				"' target='_blank'>이메일 인증 확인</a>";
		mailService.sendMail(toMail, title, htmlContent);
		redisService.deleteByKey(toMail);
		redisService.setListOps(toMail, authCode, userCreateDto.getPassword());
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
		Optional<Users> users = userRepository.findByMail(mail);
		if (users.isPresent()) {
			log.debug("UserService.checkDuplicatedMail exception occur mail: " + mail);
			throw new CustomException(ErrorCode.USER_MAIL_DUPLICATE);
		}
	}

	public ResponseEntity<ApiResponseEntity> signin(UserCreateDto userCreateDto) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(userCreateDto.getMail(), userCreateDto.getPassword()));
		Users user = userRepository.findByMail(userCreateDto.getMail())
				.orElseThrow(() -> new CustomException(ErrorCode.WRONG_MAIL_OR_PASSWORD));

		var token = jwtService.generateToken(user);
		return ApiResponseEntity.toResponseEntity(
				JwtAuthenticationResponse.builder().token(token).build());
	}

	public ResponseEntity<ApiResponseEntity> updateUserDetails(UserDetailsDto userDetailsDto) {
		Optional<Users> users = userRepository.findByMail(userDetailsDto.getMail());
		if (users.isPresent()) {
			users.get().setAge(userDetailsDto.getAge());
			users.get().setGender(userDetailsDto.getGender());
			users.get().setMajor(userDetailsDto.getMajor());
			this.userRepository.save(users.get());
			return ApiResponseEntity.toResponseEntity();
		}
		throw new CustomException(ErrorCode.USER_NOT_FOUND);
	}

	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) {
				return userRepository.findByMail(username)
						.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
			}
		};
	}
}	

