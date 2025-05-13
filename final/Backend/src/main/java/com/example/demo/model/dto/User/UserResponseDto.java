package com.example.demo.model.dto.User;

import com.example.demo.model.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    //쓴 글, 여행계획 등은 상세정보 외에 따로 찾는 걸로
    private Integer id;
    private String name;
    private String role;
    private String email;
    private String address;
    private String number;
    
    // Entity를 DTO로 변환하는 정적 메서드
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole())
                .email(user.getEmail())
                .address(user.getAddress())
                .number(user.getNumber())
                .build();
    }
}