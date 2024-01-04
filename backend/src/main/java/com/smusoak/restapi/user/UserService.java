package com.smusoak.restapi.user;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.smusoak.restapi.mail.MailService;
import com.smusoak.restapi.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final MailService mailService;
	private final RedisService redisService;
	@Value("${spring.mail.auth-code-expiration-millis}")
	private long authCodeExpirationMillis;

	public Users create(UserCreateDto userCreateDto) {
		Users user = new Users();

		user.setStudentid(userCreateDto.getStudentid());
		user.setPassword(userCreateDto.getPassword());
		this.userRepository.save(user);
		return user;
	}
	
	public Users getUser(String studentid) {
		Optional<Users> user = this.userRepository.findByStudentid(studentid);
		return user.get();
	}
	
	public List<Users> getAllUser() {
		List<Users> users = this.userRepository.findAll();
		
		return users;
	}

	public void sendCodeToMail(String studentid) {

		log.debug("UserService.sendCodeToMail studentid: " + studentid);
		this.checkDuplicatiedStudentid(studentid);
		String title = "SMUsoak 이메일 인증 번호";
		String authCode = this.createCode();
		String toMail = this.studentidToMail(studentid);
		mailService.sendMail(toMail, title, authCode);
		//redisService.setValues(toMail, authCode, Duration.ofMillis(this.authCodeExpirationMillis));
	}

	private String studentidToMail(String studentid) {
		return studentid + "@sangmyung.kr";
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
			return null;	// throw로 변경 필요
		}
	}



	private void checkDuplicatiedStudentid(String studentid) {
		Optional<Users> users = userRepository.findByStudentid(studentid);
		if (users.isPresent()) {
			log.debug("UserService.checkDuplicatedStudentid exception occur studentid: " + studentid);
			// throw 추가 필요
		}
	}
}	

