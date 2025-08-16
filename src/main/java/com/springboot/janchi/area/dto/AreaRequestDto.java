package com.springboot.janchi.area.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaRequestDto {

    private Integer numOfRows;     // 한 페이지 결과 수
    private Integer pageNo;        // 페이지 번호
    private String arrange;        // 정렬 구분 (A=제목순, C=수정일순, O=조회순 등)
    private String contentTypeId;  // 관광타입 ID (12=관광지, 32=숙박 등)
    private String areaCode;       // 지역 코드

}