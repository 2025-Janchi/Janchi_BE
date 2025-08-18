package com.springboot.janchi.janchi.repository;

import com.springboot.janchi.janchi.entity.Janchi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface JanchiRepository extends JpaRepository<Janchi, Long> {
    Optional<Janchi> findByFstvlNmAndStartDate(String fstvlNm, LocalDate startDate);
}
