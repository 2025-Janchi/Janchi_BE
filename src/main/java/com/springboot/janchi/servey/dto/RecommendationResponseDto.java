package com.springboot.janchi.servey.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponseDto {
    private List<Item> items;
    private List<String> keywords;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Item {
        private Long id;
        private String name;
        private String reason;
        private String imageUrl;
        private String fstvlStartDate;
        private String fstvlEndDate;
        private String opar;
    }
}
