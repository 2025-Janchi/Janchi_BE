package com.springboot.janchi.event.service;

import com.springboot.janchi.event.dto.WinnerResponseDto;
import com.springboot.janchi.event.entity.Event;
import com.springboot.janchi.event.entity.EventForm;
import com.springboot.janchi.event.entity.EventWinner;

import com.springboot.janchi.event.repository.EventFormRepository;
import com.springboot.janchi.event.repository.EventWinnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventWinnerService {

    private final EventService eventService;
    private final EventFormRepository formRepository;
    private final EventWinnerRepository winnerRepository;

    /**
     * 종료된 이벤트 → 기존 당첨자 있으면 반환,
     * 없으면 자동 랜덤 추첨 후 저장 & 반환
     */
    @Transactional
    public List<WinnerResponseDto> getOrPickWinners(Long eventId, int winnerCount) {
        Event event = eventService.getEntityOrThrow(eventId);
        assertEventEnded(event);

        // 이미 당첨자가 있으면 그대로 반환
        List<EventWinner> existing = winnerRepository.findByEvent_IdOrderByCreatedAtAsc(eventId);
        if (!existing.isEmpty()) {
            return existing.stream()
                    .map(w -> WinnerResponseDto.builder()
                            .name(w.getName())
                            .phoneLast4(w.getPhoneLast4())
                            .build())
                    .toList();
        }

        // 없으면 자동 랜덤 추첨
        List<EventForm> forms = formRepository.findByEventIdOrderByCreatedAtDesc(eventId);
        if (forms.isEmpty()) {
            throw new IllegalStateException("신청자가 없습니다.");
        }

        if (winnerCount > forms.size()) {
            winnerCount = forms.size();
        }

        List<EventForm> shuffled = new ArrayList<>(forms);
        Collections.shuffle(shuffled);

        List<EventForm> selected = shuffled.subList(0, winnerCount);

        List<WinnerResponseDto> result = new ArrayList<>();
        for (EventForm form : selected) {
            String phone = form.getPhone();
            String last4 = (phone != null && phone.length() >= 4)
                    ? phone.substring(phone.length() - 4)
                    : "****";

            EventWinner winner = EventWinner.builder()
                    .event(event)
                    .form(form)
                    .name(form.getName())
                    .phoneLast4(last4)
                    .build();
            winnerRepository.save(winner);

            result.add(WinnerResponseDto.builder()
                    .name(winner.getName())
                    .phoneLast4(last4)
                    .build());
        }

        return result;
    }

    private void assertEventEnded(Event event) {
        LocalDateTime now = LocalDateTime.now();
        if (event.getEndAt() == null || event.getEndAt().isAfter(now)) {
            throw new IllegalStateException("이벤트가 종료되지 않았습니다.");
        }
    }

}

