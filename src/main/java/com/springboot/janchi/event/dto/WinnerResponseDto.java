package com.springboot.janchi.event.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WinnerResponseDto {
    private String name;
    private String phoneLast4;
}
