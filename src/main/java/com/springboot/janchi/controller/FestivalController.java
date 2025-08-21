package com.springboot.janchi.controller;

import com.springboot.janchi.service.FestivalApiService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class FestivalController {
    private final FestivalApiService festivalApiService;

    //http://localhost:8080/api/public/festivals?pageNo=1&numOfRows=10

//    @Value("${publicdata.service-key}")
//    private String serviceKey;

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
private String serviceKey;

    @GetMapping("/festivals")
    public void redirectToPublicData(HttpServletResponse response,
                                     @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                     @RequestParam(required = false, defaultValue = "10") Integer numOfRows) throws IOException {
        String targetUrl = String.format(
                "http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api?serviceKey=%s&pageNo=%d&numOfRows=%d&type=json",
                serviceKey,
                pageNo,
                numOfRows
        );
        response.sendRedirect(targetUrl);
    }


}
