package com.springboot.janchi.banner.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class BannerResponseDto {

    private Long id;
    private final String banner;
}
