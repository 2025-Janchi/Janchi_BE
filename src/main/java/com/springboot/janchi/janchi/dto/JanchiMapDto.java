package com.springboot.janchi.janchi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JanchiMapDto {
    private String rdnmadr;        // 도로명주소
    private String lnmadr;         // 지번주소
    private Double latitude;       // 위도
    private Double longitude;      // 경도
}
