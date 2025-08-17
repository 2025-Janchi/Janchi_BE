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

    public List<EventResponseDto> listActive() {
        return eventRepository.findByActiveTrueOrderByStartAtAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public EventResponseDto getOne(Long eventId) {
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트가 존재하지 않습니다."));
        if (!e.isActive()) {
            throw new IllegalStateException("비활성화된 이벤트입니다.");
        }
        return toDto(e);
    }

    public Event getEntityOrThrow(Long eventId) {
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트가 존재하지 않습니다."));
        if (!e.isActive()) {
            throw new IllegalStateException("비활성화된 이벤트입니다.");
        }
        return e;
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
        // 이벤트 존재/활성 확인
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
