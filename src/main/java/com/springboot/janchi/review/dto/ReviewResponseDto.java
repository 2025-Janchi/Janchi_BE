package com.springboot.janchi.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private String userId;
    private Integer star;
    private String content;
    private String createDate;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ReviewResponseDto(Long id, String userId, int star, String content, Date createDate) {
        this.id = id;
        this.userId = userId;
        this.star = star;
        this.content = content;
        this.createDate = sdf.format(createDate);
    }

}
