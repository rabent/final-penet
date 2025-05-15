package com.example.demo.service;

import com.example.demo.model.dto.User.UserRequestDto;
import com.example.demo.model.dto.User.UserResponseDto;
import com.example.demo.model.entity.User;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

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
}
