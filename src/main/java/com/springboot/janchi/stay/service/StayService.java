package com.springboot.janchi.stay.service;


import com.springboot.janchi.stay.dto.StayResponseDto;
import com.springboot.janchi.stay.entity.Stay;
import com.springboot.janchi.stay.repository.StayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class StayService {
    private final StayRepository stayRepository;

    @Transactional
    public int saveStays(List<StayResponseDto.StayResponse.StayItem> items) {
        List<Stay> entities = items.stream()
                .map(Stay::fromStayItem)
                .toList();
        stayRepository.saveAll(entities);
        return entities.size();

    }
}
