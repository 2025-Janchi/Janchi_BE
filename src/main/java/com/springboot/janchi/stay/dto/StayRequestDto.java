package com.springboot.janchi.stay.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StayRequestDto {

    private Integer numOfRows;     // 한 페이지 결과 수
    private Integer pageNo;        // 페이지 번호

    private String arrange;        // 정렬 구분 (A=제목순, C=수정일순, O=조회순 등)

    private String areaCode;       // 지역 코드
    private String sigunguCode;    // 시군구코드, areaCode 필수 입력

    private String lDongRegnCd; // 법정동 시도코드
    private String lDongSignguCd; // 법정동 시군구코드, 시도코드 필수 입력



}
