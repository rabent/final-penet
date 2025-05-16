package com.example.demo.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@Builder
@EqualsAndHashCode(exclude = "user")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Integer id;
    
    // User 엔티티와의 다대일(N:1) 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true) // 외래 키 컬럼명
    private User user;

    @OneToMany(mappedBy="board",fetch = FetchType.LAZY)
    @Builder.Default
    private List<BImage> images=new ArrayList<>();
    
    @Column(name = "title", length = 30, nullable = false)
    private String title;
    
    @Column(name = "content", length = 900, nullable = false)
    private String content;
    
    @Column(name = "hit")
    private Integer hit;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void addImage(BImage image) {
        this.images.add(image);
        image.setBoard(this);
    }
}