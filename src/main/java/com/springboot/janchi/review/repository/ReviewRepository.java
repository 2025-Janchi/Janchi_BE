package com.springboot.janchi.review.repository;

import com.springboot.janchi.janchi.entity.Janchi;
import com.springboot.janchi.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByIdAndUserIdAndPassword(Long id, String userId, String password);
    List<Review> findByJanchi(Janchi janchi);
    // 별점 조회
    @Query("SELECT AVG (r.star) FROM Review r")
    Double findAverageStar();

    @Query("SELECT AVG(r.star) FROM Review r WHERE r.janchi = :janchi")
    Double findAvgStarByFestival(@Param("janchi") Janchi janchi);


}

