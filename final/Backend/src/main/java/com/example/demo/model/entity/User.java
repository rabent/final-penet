package com.example.demo.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "\"user\"")
@Getter // @Data 대신
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "boards")
@EqualsAndHashCode(exclude = "boards")
public class User {

    @Id  // 기본키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가 전략
    @Column(name = "user_id")
    private Integer id;
    
    @Column(name = "name", length = 15, nullable = false)
    private String name;
    
    @Column(name = "password", length = 15, nullable = false)
    private String password;
    
    @Column(name = "role", length = 10, nullable = false)
    private String role;
    
    @Column(name = "email", length = 40, nullable = false)
    private String email;
    
    @Column(name = "address", length = 50, nullable = false)
    private String address;
    
    @Column(name = "number", length = 30, nullable = false)
    private String number;

    @OneToMany(mappedBy = "user", orphanRemoval = false)
    @Builder.Default
    private List<Board> boards=new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL ,orphanRemoval = true)
    @Builder.Default
    private List<TripPlan> plans=new ArrayList<>();

    public void addBoard(Board board) {
        this.boards.add(board);
        board.setUser(this);
    }

    public void addPlan(TripPlan plan) {
        this.plans.add(plan);
        plan.setUser(this);
    }
} 