package com.springboot.janchi.festival.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.janchi.festival.dto.FestivalResponse;
import com.springboot.janchi.festival.dto.JanchiDetailDto;
import com.springboot.janchi.festival.service.JanchiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FestivalController {
//    private final FestivalApiService festivalApiService;

    private static final Logger log = LoggerFactory.getLogger(FestivalController.class);

    @Value("${publicdata.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JanchiService janchiService;

    @GetMapping("/festivals")
    public ResponseEntity<?> getFestivalSummaries(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "100") Integer numOfRows,
            @RequestParam(defaultValue = "true") boolean onlyThisYear,
            @RequestParam(defaultValue = "false") boolean save
    ) {
        // 0) 키 준비
        String rawKey = serviceKey == null ? "" : serviceKey.trim();
        if (rawKey.isEmpty()) {
            return ResponseEntity.status(500).body(Map.of("error", "serviceKey is empty"));
        }
        String keyParam = rawKey.contains("%") ? rawKey : URLEncoder.encode(rawKey, StandardCharsets.UTF_8);

        // 1) URL
        String url = "http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api"
                + "?pageNo=" + pageNo
                + "&numOfRows=" + numOfRows
                + "&type=json"
                + "&serviceKey=" + keyParam;

        try {
            String masked = keyParam.substring(0, Math.min(6, keyParam.length())) + "...";
            log.debug("PUBLICDATA URL: {}", url.replace(keyParam, masked));
        } catch (Exception ignore) {}

        try {
            // 2) 호출
            String json = restTemplate.getForObject(URI.create(url), String.class);
            if (json == null) return ResponseEntity.ok(Collections.emptyList());

            JsonNode root = objectMapper.readTree(json);

            // 3) 결과 코드
            String resultCode = root.at("/response/header/resultCode").asText("");
            if (!"00".equals(resultCode)) {
                return ResponseEntity.ok(root);
            }

            // 4) items 파싱
            List<FestivalResponse> list = new ArrayList<>();
            JsonNode items = root.at("/response/body/items");
            if (items.isArray()) {
                for (JsonNode it : items) {
                    if (it.has("item")) {
                        JsonNode inner = it.get("item");
                        if (inner.isArray()) for (JsonNode x : inner) list.add(FestivalResponse.from(x));
                        else if (inner.isObject()) list.add(FestivalResponse.from(inner));
                    } else {
                        list.add(FestivalResponse.from(it));
                    }
                }
            } else {
                JsonNode itemNode = root.at("/response/body/items/item");
                if (itemNode.isArray()) for (JsonNode it : itemNode) list.add(FestivalResponse.from(it));
                else if (itemNode.isObject()) list.add(FestivalResponse.from(itemNode));
            }

            // 5) 필터/정렬
            if (onlyThisYear) {
                LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
                int year = today.getYear();

                // 한 번만 파싱해서 캐싱
                record NormFestival(FestivalResponse raw, LocalDate s, LocalDate e) {}
                List<NormFestival> norm = list.stream()
                        .map(f -> new NormFestival(
                                f,
                                JanchiService.parseDateFirst(f.getFstvlStartDate()),
                                JanchiService.parseDateLast(f.getFstvlEndDate())
                        ))
                        .toList();

                list = norm.stream()
                        .filter(n -> {
                            if (n.s() == null) return false;
                            boolean upcomingThisYear = (n.s().getYear() == year) && !n.s().isBefore(today);
                            boolean ongoingNowThisYear = (n.s().getYear() == year) && !n.s().isAfter(today) && (n.e() == null || !n.e().isBefore(today));
                            return upcomingThisYear || ongoingNowThisYear;
                        })
                        .sorted(
                                Comparator
                                        .comparingInt((NormFestival n) -> {
                                            boolean ongoing = (n.s() != null) && !n.s().isAfter(today) && (n.e() == null || !n.e().isBefore(today));
                                            return ongoing ? 0 : 1; // 진행중 우선
                                        })
                                        .thenComparing(NormFestival::s, Comparator.nullsLast(Comparator.naturalOrder()))
                                        .thenComparing(n -> n.raw().getFstvlNm(), Comparator.nullsLast(String::compareTo))
                        )
                        .map(NormFestival::raw)
                        .toList();
            }

            // 6) 저장 옵션
            if (save) {
                int saved = janchiService.upsertAll(list);
                Map<String, Object> payload = new LinkedHashMap<>();
                payload.put("savedCount", saved);
                payload.put("items", list);
                return ResponseEntity.ok(payload);
            }

            return ResponseEntity.ok(list);

        } catch (Exception e) {
            log.warn("Festival API error", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/janchi/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        try {
            JanchiDetailDto dto = janchiService.getDetail(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    java.util.Map.of("error","NOT_FOUND","message", e.getMessage())
            );
        }
    }
}
