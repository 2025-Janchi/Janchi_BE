package com.springboot.janchi.stay.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.janchi.area.dto.AreaResponseDto;
import com.springboot.janchi.area.entity.Area;
import com.springboot.janchi.stay.dto.StayMapDto;
import com.springboot.janchi.stay.dto.StayResponseDto;
import com.springboot.janchi.stay.entity.Stay;
import com.springboot.janchi.stay.repository.StayRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StayController {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final StayRepository stayRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${area.api.encoding.key}")
    private String serviceKey;

    @GetMapping("/stay")
    public ResponseEntity<List<StayResponseDto.StayResponse.StayItem>> redirectToStayApi(
            @RequestParam(defaultValue = "O") String arrange,
            @RequestParam(required = false) String areaCode,
            @RequestParam(defaultValue = "100") String numOfRows,
            @RequestParam(defaultValue = "1") String pageNo
    ) throws Exception {
        UriComponentsBuilder ub = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/searchStay2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("MobileApp", "AppTest")
                .queryParam("MobileOS", "ETC")
                .queryParam("_type", "json")
                .queryParam("arrange", arrange)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo);

        //if (StringUtils.hasText(contentTypeId)) ub.queryParam("contentTypeId", contentTypeId);
        if (StringUtils.hasText(areaCode)) ub.queryParam("areaCode", areaCode);

        // false로 인코딩 다시 하는 거 방지 + 아래 uri.create도 같이 써주기
        String targetUrl = ub.build(false).toUriString();
        String response = restTemplate.getForObject(URI.create(targetUrl), String.class);

        StayResponseDto.StayResponse ar =
                objectMapper.readValue(response, StayResponseDto.StayResponse.class);

        List<StayResponseDto.StayResponse.StayItem> items =
                ar != null
                        && ar.getResponse() != null
                        && ar.getResponse().getBody() != null
                        && ar.getResponse().getBody().getItems() != null
                        ? ar.getResponse().getBody().getItems().getItem()
                        : List.of();

        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Stay> entities = items.stream()
                .map(Stay::fromStayItem)
                .toList();

        stayRepository.saveAll(entities);


        return ResponseEntity.ok(items);
    }

    @GetMapping("/stay/map")
    public ResponseEntity<List<StayMapDto>> getStayMapOnly(
            @RequestParam(defaultValue = "O") String arrange,
            @RequestParam(required = false) String areaCode,
            @RequestParam(defaultValue = "100") String numOfRows,
            @RequestParam(defaultValue = "1") String pageNo
    ) throws Exception {

        UriComponentsBuilder ub = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/searchStay2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("MobileApp", "AppTest")
                .queryParam("MobileOS", "ETC")
                .queryParam("_type", "json")
                .queryParam("arrange", arrange)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo);

        if (StringUtils.hasText(areaCode)) ub.queryParam("areaCode", areaCode);

        String targetUrl = ub.build(false).toUriString();
        String response = restTemplate.getForObject(URI.create(targetUrl), String.class);

        StayResponseDto.StayResponse ar =
                objectMapper.readValue(response, StayResponseDto.StayResponse.class);

        List<StayResponseDto.StayResponse.StayItem> items =
                ar != null
                        && ar.getResponse() != null
                        && ar.getResponse().getBody() != null
                        && ar.getResponse().getBody().getItems() != null
                        ? ar.getResponse().getBody().getItems().getItem()
                        : List.of();

        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<StayMapDto> mapDtos = items.stream()
                .map(item -> new StayMapDto(item.getAddr1(), item.getAddr2(),item.getMapx(), item.getMapy()))
                .toList();

        return ResponseEntity.ok(mapDtos);
    }


}
