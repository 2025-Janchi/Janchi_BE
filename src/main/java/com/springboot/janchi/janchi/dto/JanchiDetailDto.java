package com.springboot.janchi.janchi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class JanchiDetailDto {
    private Long id;
    private String fstvlNm;        // 축제명
    private String opar;           // 개최장소
    private LocalDate startDate;   // 시작일
    private LocalDate endDate;     // 종료일
    private String fstvlCo;        // 내용

    private String mnnstNm;        // 주관기관
    private String auspcInsttNm;   // 주최기관
    private String suprtInsttNm;   // 후원기관
    private String phoneNumber;    // 전화번호
    private String homepageUrl;    // 홈페이지
    private String relateInfo;     // 관련정보
    private String rdnmadr;        // 도로명주소
    private String lnmadr;         // 지번주소
    private Double latitude;       // 위도
    private Double longitude;      // 경도
    private LocalDate referenceDate; // 기준일

    // 파생값
    private boolean ongoing;       // 진행중 여부
    private Integer dday;          // D-day (진행중=0, 끝난 경우=null)
    private Integer duration;      // 진행일수

    private String image;

}
