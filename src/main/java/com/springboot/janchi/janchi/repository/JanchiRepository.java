package com.springboot.janchi.janchi.repository;

import com.springboot.janchi.janchi.entity.Janchi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JanchiRepository extends JpaRepository<Janchi, Long> {
    Optional<Janchi> findByFstvlNmAndStartDate(String fstvlNm, LocalDate startDate);

    /// 진행중이거나 앞으로 시작하는 잔치 중, 시작일 빠른 순 12개
    List<Janchi> findTop12ByEndDateGreaterThanEqualOrStartDateGreaterThanEqualOrderByStartDateAsc(
            LocalDate todayForEnd, LocalDate todayForStart
    );

    default List<Janchi> findUpcomingTop12(LocalDate today) {
        return findTop12ByEndDateGreaterThanEqualOrStartDateGreaterThanEqualOrderByStartDateAsc(today, today);
    }
}
