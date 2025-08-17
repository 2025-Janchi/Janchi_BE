package com.springboot.janchi.event.service;

import com.springboot.janchi.event.dto.FormRequestDto;
import com.springboot.janchi.event.dto.FormResponseDto;
import com.springboot.janchi.event.entity.Event;
import com.springboot.janchi.event.entity.EventForm;
import com.springboot.janchi.event.repository.EventFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventFormService {
    private final EventService eventService;
    private final EventFormRepository formRepository;

    @Transactional
    public FormResponseDto submit(Long eventId, FormRequestDto req) {
        Event event = eventService.getEntityOrThrow(eventId);

        if (formRepository.existsByEventAndEmail(event, req.getEmail())) {
            throw new IllegalStateException("이미 해당 이메일로 신청되었습니다.");
        }

        EventForm saved = formRepository.save(
                EventForm.builder()
                        .event(event)
                        .name(req.getName())
                        .email(req.getEmail())
                        .phone(req.getPhone())
                        .ageGroup(req.getAgeGroup())
                        .address(req.getAddress())
                        .build()
        );

        return FormResponseDto.builder()
                .formId(saved.getId())
                .eventId(event.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .build();
    }
}
