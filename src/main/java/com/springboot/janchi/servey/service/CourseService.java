package com.springboot.janchi.servey.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.janchi.janchi.entity.Janchi;
import com.springboot.janchi.janchi.repository.JanchiRepository;
import com.springboot.janchi.servey.dto.CourseResponseDto;
import com.springboot.janchi.servey.dto.GeminiReqDto;
import com.springboot.janchi.servey.dto.GeminiResDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final JanchiRepository janchiRepository;
    private final ObjectMapper om = new ObjectMapper();
    private final RestTemplate geminiRestTemplate;

    @Value("${google.gemini.api-key}")
    private String geminiApiKey;

    @Transactional(readOnly = true)
    public CourseResponseDto getCourse(Long janchiId) throws IOException {
        Janchi j = janchiRepository.findById(janchiId)
                .orElseThrow(() -> new EntityNotFoundException("잔치 없음"));

        String prompt = """
      너는 한국 여행 가이드야. 아래의 축제에 참여하는 여행자를 위해
      해당 축제를 포함한 **여행 코스(총 4개 장소)**를 제안해줘.

      조건:
      - 반드시 해당 축제를 코스에 포함.
      - 나머지 3개는 근처에서 관광/체험/음식 관련 추천.
      - 각 장소는 이름(name), 설명(description), 위도(lat), 경도(lng)를 포함.
      - 위도, 경도는 실제 구글 지도 기준 좌표로 제공.

      축제 정보:
      - 이름: %s
      - 위치: %s
      - 일정: %s ~ %s
      - 설명: %s

      반드시 JSON 으로만 응답:
      {
        "course": [
          {"name": "<장소1>", "description": "<설명>", "lat": <float>, "lng": <float>},
          {"name": "<장소2>", "description": "<설명>", "lat": <float>, "lng": <float>},
          {"name": "<장소3>", "description": "<설명>", "lat": <float>, "lng": <float>},
          {"name": "<장소4>", "description": "<설명>", "lat": <float>, "lng": <float>}
        ]
      }
    """.formatted(
                j.getFstvlNm(), j.getOpar(),
                j.getStartDate(), j.getEndDate(),
                nz(j.getFstvlCo())
        );

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;
        GeminiReqDto body = GeminiReqDto.ofPrompt(prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GeminiReqDto> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<GeminiResDto> resp =
                geminiRestTemplate.postForEntity(url, httpEntity, GeminiResDto.class);

        String json = Optional.ofNullable(resp.getBody())
                .map(GeminiResDto::getFirstText)
                .orElse("{}");

        json = extractJson(json);

        Map<String, Object> root = om.readValue(json, new TypeReference<>() {});
        List<Map<String, Object>> course =
                (List<Map<String, Object>>) root.getOrDefault("course", List.of());

        List<CourseResponseDto.Place> places = course.stream().map(m ->
                CourseResponseDto.Place.builder()
                        .name(String.valueOf(m.get("name")))
                        .description(String.valueOf(m.get("description")))
                        .lat(Double.parseDouble(String.valueOf(m.get("lat"))))
                        .lng(Double.parseDouble(String.valueOf(m.get("lng"))))
                        .image(String.valueOf(m.get("image")))
                        .build()
        ).toList();

        return CourseResponseDto.builder().course(places).build();
    }

    // JSON 부분만 추출
    public static String extractJson(String input) {
        Pattern pattern = Pattern.compile("\\{.*}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return "{}";
    }

    // Null-safe 문자열
    public static String nz(String s) {
        return (s == null || s.isBlank()) ? "" : s;
    }
}
