package com.springboot.janchi.event.dto;

import com.springboot.janchi.event.entity.EventForm;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormListDto {
    private Long formId;
    private String name;
    private String email;
    private String phone;
    private EventForm.AgeGroup ageGroup;
    private String address;
    private LocalDateTime createdAt;
}
