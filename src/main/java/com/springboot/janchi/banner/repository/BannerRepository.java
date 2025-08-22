package com.springboot.janchi.banner.repository;

import com.springboot.janchi.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {
}

