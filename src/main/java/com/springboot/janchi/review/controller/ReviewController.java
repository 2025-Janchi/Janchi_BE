package com.springboot.janchi.review.controller;

import com.springboot.janchi.review.dto.ReviewRequestDto;
import com.springboot.janchi.review.dto.ReviewResponseDto;
import com.springboot.janchi.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create/review")
    public ResponseEntity<ReviewResponseDto> create(
            @RequestBody ReviewRequestDto dto) {
        return ResponseEntity.ok(reviewService.createReview(dto));
    }

    @GetMapping("/review")
    public ResponseEntity<List<ReviewResponseDto>> list() {
        return ResponseEntity.ok(reviewService.getReviewList());
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ReviewResponseDto> update(
            @PathVariable Long id,
            @RequestParam String userId,
            @RequestParam String password,
            @RequestBody ReviewRequestDto dto) {
        return ResponseEntity.ok(reviewService.updateReview(id, userId, password, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam String userId,
            @RequestParam String password) {
        reviewService.deleteReview(id, userId, password);
        return ResponseEntity.ok().build();
    }

// 전체 평균 구하기
//    @GetMapping("/avgStar")
//    public ResponseEntity<Double> getAverageStar(){
//        return ResponseEntity.ok(reviewService.getAverageStar());
//    }

    @GetMapping("review/janchi/{festivalId}")
    public ResponseEntity<List<ReviewResponseDto>> getByFestival(@PathVariable Long festivalId) {
        return ResponseEntity.ok(reviewService.getReviewsByFestival(festivalId));
    }

    @GetMapping("review/janchi/avg-star/{festivalId}")
    public ResponseEntity<Double> getAvgStar(@PathVariable Long festivalId) {
        return ResponseEntity.ok(reviewService.getAvgStarByFestival(festivalId));
    }

}
