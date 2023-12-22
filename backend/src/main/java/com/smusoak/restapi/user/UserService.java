package com.smusoak.restapi.user;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

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
}	

