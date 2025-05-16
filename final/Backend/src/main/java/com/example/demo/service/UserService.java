package com.example.demo.service;

import com.example.demo.model.dto.User.UserRequestDto;
import com.example.demo.model.dto.User.UserResponseDto;
import com.example.demo.model.dto.User.UserUpdateDto;
import com.example.demo.model.entity.Board;
import com.example.demo.model.entity.User;
import com.example.demo.repository.BoardRepository;
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

    public Integer register(UserRequestDto dto ) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
        }
        User user = dto.toEntity();
        return userRepository.save(user).getId();
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
}
