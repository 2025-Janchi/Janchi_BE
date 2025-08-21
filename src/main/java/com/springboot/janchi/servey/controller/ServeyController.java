package com.springboot.janchi.servey.controller;

import com.springboot.janchi.servey.dto.RecommendationResponseDto;
import com.springboot.janchi.servey.dto.SurveyRequestDto;
import com.springboot.janchi.servey.service.JanchiRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class ServeyController {
    private final JanchiRecommendService service;

    @PostMapping
    public ResponseEntity<RecommendationResponseDto> recommend(@RequestBody SurveyRequestDto req) {
        return ResponseEntity.ok(service.recommend(req));
    }

}
