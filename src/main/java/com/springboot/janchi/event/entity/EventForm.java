package com.springboot.janchi.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "form")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AgeGroup ageGroup;

    @Column(nullable = false, length = 255)
    private String address;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum AgeGroup {
        TEEN_10S, AGE_20S, AGE_30S, AGE_40S, AGE_50S_PLUS
    }
}
