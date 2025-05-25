package com.example.demo.service;

import com.example.demo.model.dto.User.FindPasswordRequestDto;
import com.example.demo.model.dto.User.UserRequestDto;
import com.example.demo.model.dto.User.UserResponseDto;
import com.example.demo.model.dto.User.UserUpdateDto;
import com.example.demo.model.entity.Board;
import com.example.demo.model.entity.User;
import com.example.demo.repository.BoardRepository;
import com.example.demo.util.PasswordGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final PasswordGenerator passwordGenerator;

    public Integer register(UserRequestDto dto ) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
        }
        User user = dto.toEntity();
        return userRepository.save(user).getId();
    }

    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserResponseDto userDetail(Integer id) {
        User user=userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponseDto.from(user);
    }

    public UserResponseDto userUpdate(UserUpdateDto dto, Integer id) {
        User user=userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponseDto.from(dto.updateEntity(user));
    }

    public void userDelete(Integer id) {
        User user=userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        List<Board> userBoards =user.getBoards();
        for (Board board : userBoards) {
            board.setUser(null);
        }

        userRepository.deleteById(id);
    }

    public UserResponseDto login(String email, String password) {
        Optional<User> user = userRepository.findByEmailAndPassword(email, password);
        if(user.isEmpty()) {
            throw new IllegalArgumentException("가입된 이메일이 없습니다.");
        }

        return UserResponseDto.from(user.get());

    }

    public UserResponseDto findUserEmail(String name, String number) {
        // 입력값 검증
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름을 입력해주세요.");
        }
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("전화번호를 입력해주세요.");
        }

        Optional<User> userOptional = userRepository.findByNameAndNumber(name,number);

        if(userOptional.isEmpty()) {
            throw new IllegalArgumentException("가입된 이메일이 없습니다.");
        } else {
            return UserResponseDto.from(userOptional.get());
        }
    }

    /**
     * 비밀번호 재설정 - 이메일과 이름으로 사용자 확인 후 임시 비밀번호 발급
     * @param request 이메일과 이름이 담긴 요청 DTO
     * @return PasswordResetResponseDto 임시 비밀번호 또는 실패 정보
     */
    public String resetPassword(FindPasswordRequestDto request) {
        // 입력값 검증
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }

        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("이름을 입력해주세요.");
        }

        Optional<User> userOptional = userRepository.findByEmailAndName(
                request.getEmail(),
                request.getName()
        );

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("일치하는 사용자 정보를 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        // 임시 비밀번호 생성
        String tempPassword = passwordGenerator.generateTempPassword();

        // 사용자의 비밀번호를 임시 비밀번호로 변경
        user.setPassword(tempPassword);
        userRepository.save(user);

        return tempPassword;
    }

}
