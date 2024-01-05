package com.smusoak.restapi.user;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.smusoak.restapi.mail.MailService;
import com.smusoak.restapi.redis.RedisService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import javax.swing.text.html.Option;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
	private static final int AUTH_CODE_INDEX = 0;
	private static final int PASSWORD_INDEX = 1;
	private final UserRepository userRepository;
	private final MailService mailService;
	private final RedisService redisService;
	@Value("${spring.mail.auth-code-expiration-millis}")
	private long authCodeExpirationMillis;

	public boolean createUser(String mail) {
		Users user = new Users();
		String password = redisService.getListOpsByIndex(mail, PASSWORD_INDEX);
		if(password != null) {
			user.setMail(mail);
			user.setPassword(password);
			user.setMailAuth(true);
			this.userRepository.save(user);
			return true;
		}
		return false;
	}
	
	public Users getUser(String mail) {
		Optional<Users> user = this.userRepository.findByMail(mail);
		return user.get();
	}
	
	public List<Users> getAllUser() {
		List<Users> users = this.userRepository.findAll();
		
		return users;
	}


	public void sendCodeToMail(UserCreateDto userCreateDto) throws Exception {
		String toMail = userCreateDto.getMail();
		String title = "SMUsoak 이메일 인증 번호";
		String authCode = this.createCode();
		String htmlContent = "<h1>SMUsoak 메일인증</h1>" +
				"<br>SMUsoak에 오신것을 환영합니다!" +
				"<br>아래 [이메일 인증 확인]을 눌러주세요." +
				"<br><a href='http://localhost:8080/user/mailVerification?mail=" +
				toMail + "&authCode=" + authCode +
				"' target='_blank'>이메일 인증 확인</a>";
		this.checkDuplicatiedMail(toMail);
		mailService.sendMail(toMail, title, htmlContent);
		redisService.setListOps(toMail, authCode, userCreateDto.getPassword());
	}

	public boolean verifiedCode(String mail, String authCode) {
		String redisAuthCode = redisService.getListOpsByIndex(mail, AUTH_CODE_INDEX);
		boolean authResult = redisAuthCode != null && redisAuthCode.equals(authCode);
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
			return null;	// throw로 변경 필요
		}
	}

	private void checkDuplicatiedMail(String mail) {
		Optional<Users> users = userRepository.findByMail(mail);
		if (users.isPresent()) {
			log.debug("UserService.checkDuplicatedMail exception occur mail: " + mail);
			// throw 추가 필요
		}
	}
}	

