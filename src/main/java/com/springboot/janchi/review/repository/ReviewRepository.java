package com.springboot.janchi.review.repository;

import com.springboot.janchi.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByIdAndUserIdAndPassword(Long id, String userId, String password);

    // 별점 조회
    @Query("SELECT AVG (r.star) FROM Review r")
    Double findAverageStar();
}

