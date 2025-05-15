package com.example.demo.model.dto.User;

import com.example.demo.model.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(max = 15, message = "이름은 15자 이하로 입력해주세요.")
    private String name;
    
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하로 입력해주세요.")
    private String password;
    
    @NotBlank(message = "역할은 필수 입력값입니다.")
    private String role;
    
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 40, message = "이메일은 40자 이하로 입력해주세요.")
    private String email;
    
    @NotBlank(message = "주소는 필수 입력값입니다.")
    @Size(max = 50, message = "주소는 50자 이하로 입력해주세요.")
    private String address;
    
    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    @Size(max = 30, message = "전화번호는 30자 이하로 입력해주세요.")
    private String number;
    
    // Entity 변환 메서드
    public User toEntity() {
        return User.builder()
                .password(password)
                .name(name)
                .role(role)
                .email(email)
                .address(address)
                .number(number)
                .build();
    }
}