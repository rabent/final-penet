package com.example.demo.service;

import com.example.demo.model.dto.User.UserRequestDto;
import com.example.demo.model.dto.User.UserResponseDto;
import com.example.demo.model.entity.User;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public Integer register(UserRequestDto dto ) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
        }
        User user = dto.toEntity();
        return userRepository.save(user).getId();
    }

    public UserResponseDto login(String email, String password) {
        Optional<User> user = userRepository.findByEmailAndPassword(email, password);
        if(user.isEmpty()) {
            throw new IllegalArgumentException("가입된 이메일이 없습니다.");
        }

        return UserResponseDto.from(user.get());
    }
}
