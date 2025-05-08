package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "b_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Integer id; 

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "file_path", length = 50, nullable = false)
    private String filePath;

    @Column(name = "file_name", length = 20, nullable = false)
    private String fileName;
}
