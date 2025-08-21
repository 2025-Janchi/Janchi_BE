package com.springboot.janchi.servey.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeminiReqDto {
    private List<Content> contents;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Part {
        private String text;
    }

    public static GeminiReqDto ofPrompt(String prompt) {
        return GeminiReqDto.builder()
                .contents(List.of(Content.builder()
                        .parts(List.of(Part.builder().text(prompt).build()))
                        .build()))
                .build();
    }
}
