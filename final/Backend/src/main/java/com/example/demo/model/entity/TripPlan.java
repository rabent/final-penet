package com.example.demo.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Entity
@Table(name = "trip_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래 키 컬럼명
    private User user;

    @OneToMany(mappedBy="plan", cascade = CascadeType.ALL ,fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<TripSnippet> snippets=new ArrayList<>();

    @Column(name="plan_name", length = 50, nullable=false)
    private String planName;

    @Column(name = "plan", length = 200, nullable = false)
    private String plan;

    public void addSnippet(TripSnippet snippet) {
        this.snippets.add(snippet);
        snippet.setPlan(this);
    }
}