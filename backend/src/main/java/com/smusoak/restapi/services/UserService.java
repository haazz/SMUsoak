package com.smusoak.restapi.services;

import com.smusoak.restapi.dto.UserDetailsDto;
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

    public ResponseEntity<ApiResponseEntity> updateUserDetails(UserDetailsDto userDetailsDto) {
        Optional<User> users = userRepository.findByMail(userDetailsDto.getMail());
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
