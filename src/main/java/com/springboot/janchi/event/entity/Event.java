package com.springboot.janchi.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 255)
    private String bannerImageUrl;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Builder.Default
    private boolean active = true;
}
