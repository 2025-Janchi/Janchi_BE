package com.springboot.janchi.area.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.janchi.area.dto.AreaResponseDto;
import com.springboot.janchi.area.entity.Area;
import com.springboot.janchi.area.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class AreaService {
    private final AreaRepository areaRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public int saveAreas(List<AreaResponseDto.AreaResponse.AreaItem> items) {
        List<Area> entities = items.stream()
                .map(Area::fromAreaItem)
                .toList();
        areaRepository.saveAll(entities);
        return entities.size();
    }
}


