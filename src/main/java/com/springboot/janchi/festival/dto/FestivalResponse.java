package com.springboot.janchi.festival.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FestivalResponse {
    private String fstvlNm;        // 축제명
    private String opar;       // 개최장소
    private String fstvlStartDate;   // 축제시작일자
    private String fstvlEndDate;     // 축제종료일자
    private String fstvlCo;     // 축제내용
    private String mnnstNm;   // 주관기관명
    private String suprtInsttNm;  //후원기관명
    private String phoneNumber;       // 홈페이지주소
    private String lnmadr;     // 소재지지번주소
    private Double latitude;    // 위도
    private Double longitude;   // 경도
    private String referenceDate;  //데이터기준일자

    public static FestivalResponse from(JsonNode it) {
        return new FestivalResponse(
                it.path("fstvlNm").asText(null),
                it.path("opar").asText(null),
                it.path("fstvlStartDate").asText(null),
                it.path("fstvlEndDate").asText(null),
                it.path("fstvlCo").asText(null),
                it.path("mnnstNm").asText(null),
                it.path("suprtInsttNm").asText(null),
                it.path("phoneNumber").asText(null),
                firstNonBlank(it.path("lnmadr").asText(""), it.path("rdnmadr").asText("")),
                parseDoubleOrNull(it.path("latitude").asText(null)),
                parseDoubleOrNull(it.path("longitude").asText(null)),
                it.path("referenceDate").asText(null)
        );
    }
    private static String firstNonBlank(String a, String b){ return (a!=null && !a.isBlank())?a:((b!=null && !b.isBlank())?b:null); }
    private static Double parseDoubleOrNull(String v){ if(v==null||v.isBlank())return null; try{return Double.valueOf(v.trim());}catch(Exception e){return null;} }
}
