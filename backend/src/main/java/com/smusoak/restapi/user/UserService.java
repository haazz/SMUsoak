package com.smusoak.restapi.user;

import java.util.List;
import java.util.Optional;

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
	
	public Users getUser(String studentid) {
		Optional<Users> user = this.userRepository.findByStudentid(studentid);
		return user.get();
	}
	
	public List<Users> getAllUser() {
		List<Users> users = this.userRepository.findAll();
		
		return users;
	}
}	

