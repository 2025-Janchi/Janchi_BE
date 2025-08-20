package com.springboot.janchi.stay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StayMapDto {

    private String addr1;
    private String addr2;

    private String mapx;
    private String mapy;
}
