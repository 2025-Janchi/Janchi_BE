package com.springboot.janchi.event.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormResponseDto {
    private Long formId;
    private Long eventId;
    private String name;
    private String email;
    private String phone;
}
