package com.springboot.janchi.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ReviewRequestDto {
    private String userId;
    private String password;
    private String content;
    private Integer star;

}
