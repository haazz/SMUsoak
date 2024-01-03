package com.smusoak.restapi.user;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	
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

	private void sendCodeToMail(String toMail) {

	}

	private void checkDuplicatiedStudentid(String studentid) {
		Optional<Users> users = userRepository.findByStudentid(studentid);
		if (users.isPresent()) {
			log.debug("UserService.checkDuplicatedStudentid exception occur studentid: " + studentid);
			// throw 추가 필요
		}
	}
}	

