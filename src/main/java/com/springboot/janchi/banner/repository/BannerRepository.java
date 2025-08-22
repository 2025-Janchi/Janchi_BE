package com.springboot.janchi.banner.repository;

import com.springboot.janchi.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    Optional<Banner> findFirstByJanchi_IdOrderByIdDesc(Long janchiId);
}

