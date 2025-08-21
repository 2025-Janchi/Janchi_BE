package com.springboot.janchi.servey.service;

import com.fasterxml.jackson.databind.ObjectMapper;;
import com.springboot.janchi.janchi.entity.Janchi;
import com.springboot.janchi.janchi.repository.JanchiRepository;
import com.springboot.janchi.servey.dto.GeminiReqDto;
import com.springboot.janchi.servey.dto.GeminiResDto;
import com.springboot.janchi.servey.dto.RecommendationResponseDto;
import com.springboot.janchi.servey.dto.SurveyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JanchiRecommendService {

    private final JanchiRepository janchiRepository;
    private final ObjectMapper om = new ObjectMapper();
    private final RestTemplate geminiRestTemplate;

    @Value("${google.gemini.api-key}")
    private String geminiApiKey;

    @Transactional(readOnly = true)
    public RecommendationResponseDto recommend(SurveyRequestDto req) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 1) 앞으로 열릴 12개
        List<Janchi> upcoming = janchiRepository.findUpcomingTop12(today);
        if (upcoming.isEmpty()) {
            return RecommendationResponseDto.builder().items(List.of()).build();
        }

        // 2) 설문 키워드 정규화
        String k1 = norm(req.getNatureOrCity());
        String k2 = norm(req.getMountainOrSea());
        String k3 = norm(req.getQuietOrCrowded());

        // 3) 자바 스코어링
        record WithScore(Janchi j, double s) {}
        List<WithScore> cand = upcoming.stream()
                .map(j -> new WithScore(j, score(j, req, k1, k2, k3, today)))
                .sorted(Comparator.comparingDouble(WithScore::s).reversed())
                .toList();

        Map<Long, Janchi> byId = cand.stream().map(WithScore::j)
                .collect(Collectors.toMap(Janchi::getId, j -> j));

        // 4) 후보 JSON
        String itemsJson;
        try {
            itemsJson = om.writeValueAsString(
                    cand.stream().map(ws -> Map.of(
                            "id", ws.j().getId(),
                            "name", ws.j().getFstvlNm(),
                            "region", nz(ws.j().getOpar()),
                            "startDate", String.valueOf(ws.j().getStartDate()),
                            "endDate", String.valueOf(ws.j().getEndDate()),
                            "desc", nz(ws.j().getFstvlCo()),
                            "preScore", ws.s()
                    )).toList()
            );
        } catch (Exception e) {
            itemsJson = "[]";
        }

        String prompt = """
          너는 여행 플래너야. 설문과 '앞으로 열릴 잔치' 후보 목록(최대 12개) 중 **정확히 3개**를 골라.
          각 선택 이유는 한 줄로.

          설문:
          - 인원수: %s
          - 동반: %s
          - 기간: %s
          - 지역선호: %s
          - 테마선호: [%s, %s, %s]

          후보(JSON):
          %s

          반드시 JSON 으로만 응답:
          {"items":[
            {"id": <number>, "reason": "<한줄이유>"},
            {"id": <number>, "reason": "<한줄이유>"},
            {"id": <number>, "reason": "<한줄이유>"}
          ]}
        """.formatted(
                nz(req.getPeopleCount()), nz(req.getCompanion()),
                nz(req.getDuration()), nz(req.getRegion()),
                k1, k2, k3, itemsJson
        );

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;

            GeminiReqDto body = GeminiReqDto.ofPrompt(prompt);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GeminiReqDto> httpEntity = new HttpEntity<>(body, headers);

            ResponseEntity<GeminiResDto> resp =
                    geminiRestTemplate.postForEntity(url, httpEntity, GeminiResDto.class);
            GeminiResDto res = resp.getBody();

            String json = Optional.ofNullable(res)
                    .map(GeminiResDto::getCandidates).filter(l -> !l.isEmpty())
                    .map(c -> c.get(0)).map(GeminiResDto.Candidate::getContent)
                    .map(GeminiResDto.Content::getParts).filter(l -> !l.isEmpty())
                    .map(p -> p.get(0)).map(GeminiResDto.Part::getText)
                    .orElse("");

            json = extractJson(json);

            Map<String, Object> root = om.readValue(json, Map.class);
            List<Map<String, Object>> picks =
                    (List<Map<String, Object>>) root.getOrDefault("items", List.of());

            List<RecommendationResponseDto.Item> out = new ArrayList<>();
            for (Map<String, Object> p : picks) {
                Long id = toLong(p.get("id"));
                if (id != null && byId.containsKey(id)) {
                    Janchi j = byId.get(id);
                    out.add(RecommendationResponseDto.Item.builder()
                            .id(j.getId())
                            .name(j.getFstvlNm())
                            .imageUrl(null)
                            .reason(String.valueOf(p.getOrDefault("reason", "")))
                            .build());
                }
            }

            // 보정
            for (WithScore ws : cand) {
                if (out.size() >= 3) break;
                boolean dup = out.stream().anyMatch(i -> i.getId().equals(ws.j().getId()));
                if (!dup) {
                    out.add(RecommendationResponseDto.Item.builder()
                            .id(ws.j().getId())
                            .name(ws.j().getFstvlNm())
                            .imageUrl(null)
                            .reason("스코어 상위 보정")
                            .build());
                }
            }
            return RecommendationResponseDto.builder().items(out).build();

        } catch (Exception e) {
            // 실패 시 fallback
            List<RecommendationResponseDto.Item> out = cand.stream().limit(3)
                    .map(ws -> RecommendationResponseDto.Item.builder()
                            .id(ws.j().getId())
                            .name(ws.j().getFstvlNm())
                            .imageUrl(null)
                            .reason("모델 응답 오류로 스코어 상위 추천")
                            .build())
                    .toList();
            return RecommendationResponseDto.builder().items(out).build();
        }
    }

    // ===== 내부 함수들 =====
    private static double score(Janchi j, SurveyRequestDto r, String k1, String k2, String k3, LocalDate today) {
        double s = 0;
        if (!isBlank(r.getRegion()) && nz(j.getOpar()).contains(r.getRegion())) s += 8;
        String themeHay = (nz(j.getFstvlCo()) + " " + nz(j.getRelateInfo())).replaceAll("\\s+","");
        s += contains(themeHay, k1) ? 3 : 0;
        s += contains(themeHay, k2) ? 3 : 0;
        s += contains(themeHay, k3) ? 3 : 0;
        LocalDate sDate = j.getStartDate();
        if (sDate != null) {
            long d = ChronoUnit.DAYS.between(today, sDate);
            if (d >= 0 && d <= 7) s += 2.0;
            else if (d > 7 && d <= 30) s += 1.0;
        }
        return s;
    }

    private static String norm(String s){
        if (s==null) return "";
        return switch (s.trim()) {
            case "자연" -> "자연";
            case "도시" -> "도시";
            case "산" -> "산";
            case "바다" -> "바다";
            case "조용", "조용한 여행" -> "조용";
            case "북적인", "북적이는 여행" -> "북적인";
            default -> s.trim();
        };
    }
    private static boolean contains(String hay, String needle){
        if (isBlank(hay) || isBlank(needle)) return false;
        return hay.contains(needle);
    }
    private static boolean isBlank(String s){ return s==null || s.isBlank(); }
    private static String nz(String s){ return s==null? "" : s; }
    private static Long toLong(Object v){
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof Long l) return l;
        try { return Long.valueOf(String.valueOf(v)); } catch (Exception e){ return null; }
    }
    private static String extractJson(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.startsWith("```")) {
            int i = t.indexOf('{');
            int j = t.lastIndexOf('}');
            if (i >= 0 && j >= 0 && j > i) return t.substring(i, j + 1);
        }
        return t;
    }
}
