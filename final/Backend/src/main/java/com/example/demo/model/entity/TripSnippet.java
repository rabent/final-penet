package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trip_snippet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripSnippet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Integer id;
    
    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "schedule", length = 200, nullable = false)
    private String schedule;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "category", length = 50, nullable = false)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name="plan_id")
    private TripPlan plan;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="no")
    private Attraction attraction;
}