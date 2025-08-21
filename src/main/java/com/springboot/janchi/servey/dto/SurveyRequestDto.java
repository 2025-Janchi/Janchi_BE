package com.springboot.janchi.servey.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyRequestDto {
    private String gender;           // "여","남" (선택)
    private String peopleCount;      // "혼자","2명","3명이상"
    private String companion;        // "친구","연인","가족"
    private String duration;         // "당일치기","1박2일","2박3일"
    private String region;           // "서울/수도권","강원도","충청도","전라도","경상도","제주도"

    // 테마 3쌍에서 각 1개 선택
    private String natureOrCity;     // "자연" | "도시"
    private String mountainOrSea;    // "산"   | "바다"
    private String quietOrCrowded;   // "조용한 여행" | "북적이는 여행"
}
