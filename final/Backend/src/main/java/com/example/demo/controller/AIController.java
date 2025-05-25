package com.example.demo.controller;

import com.example.demo.model.dto.AI.AttractionTipResponseDto;
import com.example.demo.service.AICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AIController {

    private final AICommentService aiCommentService;

    @GetMapping("/tip/{attrId}")
    public ResponseEntity<AttractionTipResponseDto> getTravelTip(@PathVariable Integer attrId) {
        AttractionTipResponseDto tip = aiCommentService.generateAttractionTip(attrId);
        return ResponseEntity.ok(tip);
    }
}
