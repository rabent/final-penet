package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {//별도로 쿼리 안쓰고 entity->dto dto->entity 사용
    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);
    
    // 이름으로 사용자 찾기
    Optional<User> findByName(String name);
    
    // 이메일과 비밀번호로 사용자 찾기 (로그인에 활용)
    Optional<User> findByEmailAndPassword(String email, String password);
    
    // 특정 역할을 가진 모든 사용자 찾기
    List<User> findAllByRole(String role);

    // 이메일 존재여부 체크
    boolean existsByEmail(String email);

    // 아이디 찾기: 이름과 전화번호로 사용자 찾기
    Optional<User> findByNameAndNumber(String name, String number);

    // 비밀번호 찾기: 이메일과 이름으로 사용자 찾기
    Optional<User> findByEmailAndName(String email, String name);
}