package com.springboot.janchi.event.controller;

import com.springboot.janchi.event.dto.EventResponseDto;
import com.springboot.janchi.event.dto.FormListDto;
import com.springboot.janchi.event.dto.FormRequestDto;
import com.springboot.janchi.event.dto.FormResponseDto;
import com.springboot.janchi.event.service.EventFormService;
import com.springboot.janchi.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;
    private final EventFormService formService;

    // 활성 이벤트 목록
    @GetMapping
    public ResponseEntity<List<EventResponseDto>> list() {
        return ResponseEntity.ok(eventService.listActive());
    }

    // 이벤트 조회
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getOne(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getOne(eventId));
    }

    // 이벤트 참여 폼 제출
    @PostMapping("/{eventId}/form")
    public ResponseEntity<FormResponseDto> submitForm(@PathVariable Long eventId,
                                                      @Valid @RequestBody FormRequestDto request) {
        return ResponseEntity.ok(formService.submit(eventId, request));
    }

    // 이벤트ID 별 신청 폼 조회
    @GetMapping("/{eventId}/forms")
    public ResponseEntity<List<FormListDto>> listForms(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.listFormsByEvent(eventId));
    }
}
