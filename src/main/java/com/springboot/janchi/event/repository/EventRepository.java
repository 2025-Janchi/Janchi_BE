package com.springboot.janchi.event.repository;

import com.springboot.janchi.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByActiveTrueOrderByStartAtAsc();
}
