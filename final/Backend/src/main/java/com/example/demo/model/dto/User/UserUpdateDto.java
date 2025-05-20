package com.example.demo.model.dto.User;

import com.example.demo.model.entity.User;

import jakarta.validation.constraints.Email;
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
public class UserUpdateDto {

    @Size(max = 15, message = "이름은 15자 이하로 입력해주세요.")
    private String name;
    // 비밀번호는 변경 시에만 입력하도록 설계 (필수 아님)
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하로 입력해주세요.")
    private String password;
    
    @Size(max = 10, message = "역할은 10자 이하로 입력해주세요.")
    private String role;
    
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 40, message = "이메일은 40자 이하로 입력해주세요.")
    private String email;
    
    @Size(max = 50, message = "주소는 50자 이하로 입력해주세요.")
    private String address;
    
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    @Size(max = 30, message = "전화번호는 30자 이하로 입력해주세요.")
    private String number;
    
    
    public User updateEntity(User existingUser) {
        if (password != null && !password.isEmpty()) {
            existingUser.setPassword(password);
        }
        
        if (name != null && !name.isEmpty()) {
            existingUser.setName(name);
        }
        
        if (role != null && !role.isEmpty()) {
            existingUser.setRole(role);
        }
        
        if (email != null && !email.isEmpty()) {
            existingUser.setEmail(email);
        }
        
        if (address != null && !address.isEmpty()) {
            existingUser.setAddress(address);
        }
        
        if (number != null && !number.isEmpty()) {
            existingUser.setNumber(number);
        }
        
        return existingUser;
    }
}