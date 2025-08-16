package com.springboot.janchi.festival.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.janchi.festival.dto.FestivalResponse;
import com.springboot.janchi.festival.service.FestivalApiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class FestivalController {
    private final FestivalApiService festivalApiService;

//@Value("${publicdata.service-key}")
//private String serviceKey;
//
//    @GetMapping("/festivals")
//    public void redirectToPublicData(HttpServletResponse response,
//                                     @RequestParam(required = false, defaultValue = "1") Integer pageNo,
//                                     @RequestParam(required = false, defaultValue = "10") Integer numOfRows) throws IOException {
//        String targetUrl = String.format(
//                "http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api?serviceKey=%s&pageNo=%d&numOfRows=%d&type=json",
//                serviceKey,
//                pageNo,
//                numOfRows
//        );
//        response.sendRedirect(targetUrl);
//    }


//    @Value("${publicdata.service-key}")
//    private String serviceKey; // 디코딩키
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @GetMapping("/festivals/summary")
//    public ResponseEntity<?> getFestivalSummaries(
//            @RequestParam(defaultValue = "1") Integer pageNo,
//            @RequestParam(defaultValue = "10") Integer numOfRows) {
//
//        // 디코딩키를 URL-encode 해서 붙임(중요!)
//        String url = "http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api"
//                + "?serviceKey=" + URLEncoder.encode(serviceKey.trim(), StandardCharsets.UTF_8)
//                + "&pageNo=" + pageNo
//                + "&numOfRows=" + numOfRows
//                + "&type=json";
//
//        try {
//            String json = restTemplate.getForObject(URI.create(url), String.class);
//            if (json == null) return ResponseEntity.ok(Collections.emptyList());
//
//            JsonNode root = objectMapper.readTree(json);
//
//            String resultCode = root.at("/response/header/resultCode").asText("");
//            if (!"00".equals(resultCode)) {
//                return ResponseEntity.ok(root);
//            }
//
//            List<FestivalResponse> list = new ArrayList<>();
//            JsonNode items = root.at("/response/body/items");
//            if (items.isArray()) {
//                for (JsonNode it : items) list.add(FestivalResponse.from(it));
//            } else {
//                JsonNode itemNode = root.at("/response/body/items/item");
//                if (itemNode.isArray()) for (JsonNode it : itemNode) list.add(FestivalResponse.from(it));
//                else if (itemNode.isObject()) list.add(FestivalResponse.from(itemNode));
//            }
//            return ResponseEntity.ok(list);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
//        }
//    }

    private static final Logger log = LoggerFactory.getLogger(FestivalController.class);

    @Value("${publicdata.service-key}")
    private String serviceKey; // 인코딩키(%)든 디코딩키든 아무거나 OK

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/festivals/summary")
    public ResponseEntity<?> getFestivalSummaries(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "50") Integer numOfRows,
            @RequestParam(defaultValue = "true") boolean onlyThisYear
    ) {
        // 0) 키 준비
        String rawKey = serviceKey == null ? "" : serviceKey.trim();
        if (rawKey.isEmpty()) {
            return ResponseEntity.status(500).body(Map.of("error", "serviceKey is empty"));
        }
        // 키 자동 처리: %가 있으면 인코딩키 그대로, 없으면 디코딩키로 보고 URL-encode
        String keyParam = rawKey.contains("%")
                ? rawKey
                : URLEncoder.encode(rawKey, StandardCharsets.UTF_8);

        // 1) 최종 URL (문자열 결합: 재인코딩 방지)
        String url = "http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api"
                + "?pageNo=" + pageNo
                + "&numOfRows=" + numOfRows
                + "&type=json"
                + "&serviceKey=" + keyParam;

        // (선택) 로그로 확인—키는 일부만 마스킹
        try {
            log.debug("PUBLICDATA URL: {}", url.replace(keyParam,
                    keyParam.substring(0, Math.min(6, keyParam.length())) + "..."));
        } catch (Exception ignore) {}

        try {
            // 2) 호출 (URI로 넘겨서 재인코딩 방지)
            String json = restTemplate.getForObject(URI.create(url), String.class);
            if (json == null) return ResponseEntity.ok(Collections.emptyList());

            JsonNode root = objectMapper.readTree(json);

            // 3) 결과 코드 검증
            String resultCode = root.at("/response/header/resultCode").asText("");
            if (!"00".equals(resultCode)) {
                return ResponseEntity.ok(root); // 원본 에러 그대로 반환하여 원인 확인
            }

            // 4) items 파싱 (배열/단일)
            List<FestivalResponse> list = new ArrayList<>();
            JsonNode items = root.at("/response/body/items");
            if (items.isArray()) {
                for (JsonNode it : items) list.add(FestivalResponse.from(it));
            } else {
                JsonNode itemNode = root.at("/response/body/items/item");
                if (itemNode.isArray()) for (JsonNode it : itemNode) list.add(FestivalResponse.from(it));
                else if (itemNode.isObject()) list.add(FestivalResponse.from(itemNode));
            }

            // 5) 올해만 필터 옵션
            if (onlyThisYear) {
                int year = LocalDate.now().getYear();
                LocalDate yStart = LocalDate.of(year, 1, 1);
                LocalDate yEnd   = LocalDate.of(year, 12, 31);

                list = list.stream()
                        .filter(f -> {
                            LocalDate s = parseFlexibleDate(f.getFstvlStartDate());
                            LocalDate e = parseFlexibleDate(f.getFstvlEndDate());
                            if (s == null && e == null) return false;
                            if (s == null) s = e;
                            if (e == null) e = s;
                            return !s.isAfter(yEnd) && !e.isBefore(yStart);
                        })
                        .sorted(Comparator.comparing(
                                (FestivalResponse f) -> parseFlexibleDate(f.getFstvlStartDate()),
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ))
                        .toList();
            }

            return ResponseEntity.ok(list);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // yyyyMMdd 또는 yyyy-MM-dd 모두 허용
    private static LocalDate parseFlexibleDate(String v) {
        if (v == null || v.isBlank()) return null;
        String s = v.trim();
        try {
            if (s.matches("\\d{8}")) return LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE);
            if (s.matches("\\d{4}-\\d{2}-\\d{2}")) return LocalDate.parse(s);
        } catch (DateTimeParseException ignored) {}
        return null;
    }

}
