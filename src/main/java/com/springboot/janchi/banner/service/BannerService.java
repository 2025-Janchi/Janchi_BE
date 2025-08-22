package com.springboot.janchi.banner.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.janchi.banner.entity.Banner;
import com.springboot.janchi.banner.repository.BannerRepository;
import com.springboot.janchi.janchi.entity.Janchi;
import com.springboot.janchi.janchi.repository.JanchiRepository;
import com.springboot.janchi.banner.dto.BannerResponseDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final JanchiRepository janchiRepository;
    private final BannerRepository bannerRepository;
    private final ObjectMapper om = new ObjectMapper();
    private final RestTemplate geminiRestTemplate;

    @Value("${google.gemini.api-key}")
    private String geminiApiKey;

    @Transactional(readOnly = true)
    public BannerResponseDto getRandomBanner(Long janchiId) throws IOException {
        Janchi j = janchiRepository.findById(janchiId)
                .orElseThrow(() -> new EntityNotFoundException("잔치 없음"));

        String prompt = """
                    너는 한국 여행 홍보 전문가야. 
                    아래 잔치를 홍보하기 위한 배너 문구를 만들어줘.
                    조건:
                    - 간결하게, 아주 짧게
                    - 재미있고 시선을 끄는 문구 (이모티콘 사용 금지)
                    - 반드시 축제 내용 요약 포함
                    - JSON 형식으로만 반환:
                    {
                      "banner1": "<배너문구>",
                      "banner2": "<배너문구>"
                    }

                    축제 정보:
                    - 이름: %s
                    - 내용: %s
                """.formatted(
                j.getFstvlNm(),
                nz(j.getFstvlCo())
        );

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;
        GeminiReqDto body = GeminiReqDto.ofPrompt(prompt);
        List<BannerResponseDto> toSave = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GeminiReqDto> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<GeminiResDto> resp =
                geminiRestTemplate.postForEntity(url, httpEntity, GeminiResDto.class);

        String json = Optional.ofNullable(resp.getBody())
                .map(GeminiResDto::getFirstText)
                .orElse("{}");

        json = extractJson(json);

        Map<String, String> map = om.readValue(json, new TypeReference<>() {});
        String banner1 = map.getOrDefault("banner1", "");
        String banner2 = map.getOrDefault("banner2", "");

        Banner banner = Banner.builder()
                .banner1(banner1)
                .banner2(banner2)
                .janchi(j)
                .build();

        bannerRepository.save(banner);

        return BannerResponseDto.builder()
                .id(janchiId)
                .banner1(banner1)
                .banner2(banner2)
                .build();
    }

    @Transactional(readOnly = true)
    public BannerResponseDto getSavedBanner(Long janchiId) {
        Banner banner = bannerRepository.findFirstByJanchi_IdOrderByIdDesc(janchiId)
                .orElseThrow(() -> new EntityNotFoundException("해당 잔치의 배너가 없습니다. janchiId=" + janchiId));

        return BannerResponseDto.builder()
                .id(janchiId)
                .banner1(banner.getBanner1())
                .banner2(banner.getBanner2())
                .build();
    }

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

