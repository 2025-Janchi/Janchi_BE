package com.springboot.janchi.stay.repository;

import com.springboot.janchi.stay.entity.Stay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StayRepository extends JpaRepository<Stay, Long> {
}
