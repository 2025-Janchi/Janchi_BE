package com.springboot.janchi.servey.controller;

import com.springboot.janchi.servey.dto.CourseResponseDto;
import com.springboot.janchi.servey.dto.RecommendationResponseDto;
import com.springboot.janchi.servey.dto.SurveyRequestDto;
import com.springboot.janchi.servey.service.CourseService;
import com.springboot.janchi.servey.service.JanchiRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class ServeyController {
    private final JanchiRecommendService service;
    private final CourseService courseService;

    @PostMapping("/janchis")
    public ResponseEntity<RecommendationResponseDto> recommend(@RequestBody SurveyRequestDto req) {
        return ResponseEntity.ok(service.recommend(req));
    }

    @GetMapping("/{id}/course")
    public ResponseEntity<CourseResponseDto> getCourse(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok(courseService.getCourse(id));
    }

}
