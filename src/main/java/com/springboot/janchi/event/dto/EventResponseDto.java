package com.springboot.janchi.event.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDto {
    private Long id;
    private String title;
    private String bannerImageUrl;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private boolean active;
}
