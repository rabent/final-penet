package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.repository.TripPlanRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;
}
