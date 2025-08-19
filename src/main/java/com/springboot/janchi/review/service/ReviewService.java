package com.springboot.janchi.review.service;

import com.springboot.janchi.janchi.entity.Janchi;
import com.springboot.janchi.janchi.repository.JanchiRepository;
import com.springboot.janchi.review.dto.ReviewRequestDto;
import com.springboot.janchi.review.dto.ReviewResponseDto;
import com.springboot.janchi.review.entity.Review;
import com.springboot.janchi.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final JanchiRepository janchiRepository;

    public ReviewResponseDto createReview(ReviewRequestDto dto) {
        Janchi janchi = janchiRepository.findById(dto.getJanchiId())
                .orElseThrow(() -> new RuntimeException("[error] 잔치를 찾을 수 없습니다."));

        Review review = Review.builder()
                .janchi(janchi)
                .userId(dto.getUserId())
                .password(dto.getPassword())
                .star(dto.getStar())
                .content(dto.getContent())
                .createDate(new Date())
                .build();

        Review saved = reviewRepository.save(review);
        return new ReviewResponseDto(
                saved.getId(), saved.getUserId(),
                saved.getStar(), saved.getContent(),
                saved.getCreateDate()
        );
    }

    public List<ReviewResponseDto> getReviewList() {
        return reviewRepository.findAll().stream().map(
                r -> new ReviewResponseDto(
                        r.getId(), r.getUserId(),
                        r.getStar(), r.getContent(),
                        r.getCreateDate()
                )
        ).collect(Collectors.toList());
    }

    // 리뷰 수정
    public ReviewResponseDto updateReview(Long id, String password, ReviewRequestDto dto) {
        Review review = reviewRepository.findByIdAndPassword(id, password)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 수정할 권한이 없습니다."));

        review.setContent(dto.getContent());
        review.setStar(dto.getStar());
        Review saved = reviewRepository.save(review);

        return new ReviewResponseDto(
                saved.getId(), saved.getUserId(), saved.getStar(), saved.getContent(), saved.getCreateDate()
        );
    }

    // 리뷰 삭제
    public void deleteReview(Long id, String password) {
        Review review = reviewRepository.findByIdAndPassword(id, password)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 삭제할 권한이 없습니다."));

        reviewRepository.delete(review);
    }

    // 전체 평균 평점
    public Double getAverageStar(){
        Double avg = reviewRepository.findAverageStar();
        return avg != null ? avg : 0.0;
    }

    // 축제별 리뷰 조회
    public List<ReviewResponseDto> getReviewsByFestival(Long id) {
        Janchi janchi = janchiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Festival not found"));

        return reviewRepository.findByJanchi(janchi)
                .stream()
                .map(r -> new ReviewResponseDto(r.getId(), r.getUserId(), r.getStar(), r.getContent(), r.getCreateDate()))
                .collect(Collectors.toList());
    }

    // 축제별 평균 별점
    public Double getAvgStarByFestival(Long festivalId) {
        Janchi janchi = janchiRepository.findById(festivalId)
                .orElseThrow(() -> new RuntimeException("Festival not found"));

        Double avg = reviewRepository.findAvgStarByFestival(janchi);
        return avg != null ? avg : 0.0;
    }
}

