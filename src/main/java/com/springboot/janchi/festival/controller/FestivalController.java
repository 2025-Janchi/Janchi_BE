package com.springboot.janchi.festival.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.janchi.festival.dto.FestivalResponse;
import com.springboot.janchi.festival.service.FestivalApiService;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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


    @Value("${publicdata.service-key}")
    private String serviceKey; // 디코딩키

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/festivals/summary")
    public ResponseEntity<?> getFestivalSummaries(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer numOfRows) {

        // 디코딩키를 URL-encode 해서 붙임(중요!)
        String url = "http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api"
                + "?serviceKey=" + URLEncoder.encode(serviceKey.trim(), StandardCharsets.UTF_8)
                + "&pageNo=" + pageNo
                + "&numOfRows=" + numOfRows
                + "&type=json";

        try {
            String json = restTemplate.getForObject(URI.create(url), String.class);
            if (json == null) return ResponseEntity.ok(Collections.emptyList());

            JsonNode root = objectMapper.readTree(json);

            String resultCode = root.at("/response/header/resultCode").asText("");
            if (!"00".equals(resultCode)) {
                return ResponseEntity.ok(root);
            }

            List<FestivalResponse> list = new ArrayList<>();
            JsonNode items = root.at("/response/body/items");
            if (items.isArray()) {
                for (JsonNode it : items) list.add(FestivalResponse.from(it));
            } else {
                JsonNode itemNode = root.at("/response/body/items/item");
                if (itemNode.isArray()) for (JsonNode it : itemNode) list.add(FestivalResponse.from(it));
                else if (itemNode.isObject()) list.add(FestivalResponse.from(itemNode));
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        }
    }


}
