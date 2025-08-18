package com.springboot.janchi.event.service;

import com.springboot.janchi.event.dto.EventResponseDto;
import com.springboot.janchi.event.dto.FormListDto;
import com.springboot.janchi.event.entity.Event;
import com.springboot.janchi.event.entity.EventForm;
import com.springboot.janchi.event.repository.EventFormRepository;
import com.springboot.janchi.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepository;
    private final EventFormRepository eventFormRepository;

    // 모든 이벤트 조회
    public List<EventResponseDto> listAll() {
        return eventRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    // 단건 조회
    public EventResponseDto getOne(Long eventId) {
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트가 존재하지 않습니다."));
        return toDto(e);
    }

    public Event getEntityOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트가 존재하지 않습니다."));
    }

    private EventResponseDto toDto(Event e) {
        return EventResponseDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .bannerImageUrl(e.getBannerImageUrl())
                .description(e.getDescription())
                .startAt(e.getStartAt())
                .endAt(e.getEndAt())
                .active(e.isActive())
                .build();
    }

    public List<FormListDto> listFormsByEvent(Long eventId) {
        getEntityOrThrow(eventId);

        return eventFormRepository.findByEventIdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(this::toFormItem)
                .toList();
    }

    private FormListDto toFormItem(EventForm f) {
        return FormListDto.builder()
                .formId(f.getId())
                .name(f.getName())
                .email(f.getEmail())
                .phone(f.getPhone())
                .ageGroup(f.getAgeGroup())
                .address(f.getAddress())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
