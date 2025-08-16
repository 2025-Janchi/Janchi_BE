package com.springboot.janchi.area.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.janchi.area.dto.AreaResponseDto;
import com.springboot.janchi.area.entity.Area;
import com.springboot.janchi.area.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AreaService {
    private final AreaRepository areaRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${area.api.encoding.key}")
    private String serviceKey;

    private List<Area> getAreaData(String arrange,
                                   String contentTypeId,
                                   String areaCode,
                                   String numOfRows,
                                   String pageNo) {

        String url = "http://apis.data.go.kr/B551011/KorService2/areaBasedList2"
                + "?serviceKey=" + serviceKey
                + "&MobileApp=AppTest"
                + "&MobileOS=ETC"
                + "&arrange=" + arrange
                + "&contentTypeId=" + contentTypeId
                + "&areaCode=" + areaCode
                + "&_type=json"
                + "&numOfRows=" + numOfRows
                + "&pageNo=" + pageNo;

        // 1) DTO로 바로 받기
        ResponseEntity<AreaResponseDto.AreaResponse> resp =
                restTemplate.getForEntity(url, AreaResponseDto.AreaResponse.class);

        // 2) response → body → items → item → 엔티티 변환
        List<Area> areaList = Optional.ofNullable(resp.getBody())
                .map(AreaResponseDto.AreaResponse::getResponse)
                .map(AreaResponseDto.AreaResponse.InnerResponse::getBody)
                .map(AreaResponseDto.AreaResponse.Body::getItems)
                .map(AreaResponseDto.AreaResponse.Items::getItem)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(Area::fromAreaItem)
                .collect(Collectors.toList());

        areaRepository.saveAll(areaList);
        return areaList;
    }

    /* 이거 쓰기 */
    public List<Area> fetchAreasOnly(String arrange,
                                     String contentTypeId,
                                     String areaCode,
                                     String numOfRows,
                                     String pageNo) throws Exception {

        String url = "http://apis.data.go.kr/B551011/KorService2/areaBasedList2"
                + "?serviceKey=" + serviceKey
                + "&MobileApp=AppTest"
                + "&MobileOS=ETC"
                + "&_type=json"   // json으로 요청
                + "&arrange=" + arrange
                + "&contentTypeId=" + contentTypeId
                + "&areaCode=" + areaCode
                + "&numOfRows=" + numOfRows
                + "&pageNo=" + pageNo;

        // 1. 그냥 문자열로 받기
        String body = restTemplate.getForObject(url, String.class);

        // 2. JSON 파싱 시도
        AreaResponseDto.AreaResponse api =
                objectMapper.readValue(body, AreaResponseDto.AreaResponse.class);

        // 3. 꺼내서 엔티티 변환
        return api.getResponse()
                .getBody()
                .getItems()
                .getItem()
                .stream()
                .map(Area::fromAreaItem)
                .toList();
    }
}

