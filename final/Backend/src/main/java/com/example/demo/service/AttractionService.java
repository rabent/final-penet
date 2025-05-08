package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.repository.AttractionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttractionService {
    private final AttractionRepository attractionRepository;
}
