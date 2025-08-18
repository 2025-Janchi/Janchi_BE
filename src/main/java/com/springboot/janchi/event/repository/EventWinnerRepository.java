package com.springboot.janchi.event.repository;

import com.springboot.janchi.event.entity.EventWinner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EventWinnerRepository extends JpaRepository<EventWinner, Long> {
    List<EventWinner> findByEvent_IdOrderByCreatedAtAsc(Long eventId);
}
