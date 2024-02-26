package com.smusoak.restapi.services;

import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.response.CustomException;
import com.smusoak.restapi.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public ResponseEntity<ApiResponseEntity> updateUserDetails(UserDto.updateUserDetailsDto request) {
        Optional<User> users = userRepository.findByMail(request.getMail());
        if (users.isPresent()) {
            users.get().setAge(request.getAge());
            users.get().setGender(request.getGender());
            users.get().setMajor(request.getMajor());
            this.userRepository.save(users.get());
            return ApiResponseEntity.toResponseEntity();
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByMail(username).orElseThrow(() -> new UsernameNotFoundException("Usernam not found"));
            }
        };
    }

    public User save(User newUser) {
        if (newUser.getId() == null) {
            newUser.setCreatedAt(LocalDateTime.now());
        }
        return userRepository.save(newUser);
    }
}
