package com.example.demo.controller;

import com.example.demo.model.dto.User.UserRequestDto;
import com.example.demo.model.dto.User.UserResponseDto;
import com.example.demo.model.dto.User.UserUpdateDto;
import com.example.demo.model.entity.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRequestDto dto) {
        Integer id = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> detailUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.userDetail(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@ModelAttribute @Valid UserUpdateDto dto, @PathVariable Integer userId) {
        return ResponseEntity.ok(userService.userUpdate(dto,userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        userService.userDelete(userId);
        return ResponseEntity.noContent().build();
    }
}
