package com.springboot.janchi.servey.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDto {
    private List<Place> course;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Place {
        private String name;
        private String description;
        private double lat; //위도
        private double lng; //경도
        private String image;
    }
}
