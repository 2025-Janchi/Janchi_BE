package com.springboot.janchi.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_winner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventWinner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "form_id")
    private EventForm form;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, length = 4)
    private String phoneLast4;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
