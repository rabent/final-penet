package com.example.demo.model.entity;

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

    @OneToMany(mappedBy="tripPlan",fetch = FetchType.EAGER)
    @Singular
    private List<TripSnippet> snippets;

    @Column(name="plan_name", length = 50, nullable=false)
    private String planName;

    @Column(name = "plan", length = 200, nullable = false)
    private String plan;

    public void addSnippet(TripSnippet snippet) {
        this.snippets.add(snippet);
        snippet.setTripPlan(this);
    }
}