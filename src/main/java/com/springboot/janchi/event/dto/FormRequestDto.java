package com.springboot.janchi.event.dto;

import com.springboot.janchi.event.entity.EventForm;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormRequestDto {
    @NotBlank
    @Size(max = 60)
    private String name;

    @Email
    @NotBlank @Size(max = 120)
    private String email;

    @NotBlank @Size(max = 30)
    private String phone;

    @NotNull
    private EventForm.AgeGroup ageGroup;

    @NotBlank @Size(max = 255)
    private String address;
}
