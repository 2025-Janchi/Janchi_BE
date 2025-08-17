package com.springboot.janchi.event.repository;

import com.springboot.janchi.event.entity.Event;
import com.springboot.janchi.event.entity.EventForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventFormRepository extends JpaRepository<EventForm, Long> {
    boolean existsByEventAndEmail(Event event, String email);
    List<EventForm> findByEventIdOrderByCreatedAtDesc(Long eventId);
}
