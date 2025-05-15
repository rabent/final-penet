package com.example.demo.controller;

import com.example.demo.model.dto.User.LoginRequestDto;
import com.example.demo.model.dto.User.UserResponseDto;
import com.example.demo.security.JwtProvider;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        UserResponseDto user = userService.login(request.getEmail(), request.getPassword());
        String token = jwtProvider.createToken(user.getId().toString());

        return ResponseEntity.ok(Map.of(
                "Authorization", "Bearer " + token,
                "userId", user.getId()
        ));
    }

}
