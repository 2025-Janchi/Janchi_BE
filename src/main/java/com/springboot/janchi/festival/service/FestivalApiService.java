package com.springboot.janchi.festival.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;


@Service
@RequiredArgsConstructor
public class FestivalApiService {
//    @Value("${publicdata.service-key}")
//    private String serviceKey;
//
//    private final WebClient webClient = WebClient.builder()
//            .baseUrl("http://api.data.go.kr")
//            .build();
//
//    public String fetchFestivals(Integer pageNo, Integer numOfRows) {
//        return webClient.get()
//                .uri(uriBuilder -> {
//                    String uri = String.format(
//                            "/openapi/tn_pubr_public_cltur_fstvl_api?serviceKey=%s&pageNo=%d&numOfRows=%d&type=json",
//                            serviceKey,
//                            pageNo != null ? pageNo : 1,
//                            numOfRows != null ? numOfRows : 100
//                    );
//                    return URI.create(uri);
//                })
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }
@Value("${publicdata.service-key}") // 디코딩된 키
private String serviceKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://api.data.go.kr")
            .build();

    public String fetchFestivals(Integer pageNo, Integer numOfRows) {
        String finalUrl = String.format(
                "/openapi/tn_pubr_public_cltur_fstvl_api?serviceKey=%s&pageNo=%d&numOfRows=%d&type=json",
                serviceKey,
                pageNo != null ? pageNo : 1,
                numOfRows != null ? numOfRows : 100
        );

        System.out.println("최종 호출 URL = http://api.data.go.kr" + finalUrl);

        return webClient.get()
                .uri(finalUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
