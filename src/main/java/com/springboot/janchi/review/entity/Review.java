package com.springboot.janchi.review.entity;

import com.springboot.janchi.janchi.entity.Janchi;
import com.springboot.janchi.review.dto.ReviewResponseDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userId;
    private String password;
    private Integer star;
    private Date createDate;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Janchi janchi;


}
