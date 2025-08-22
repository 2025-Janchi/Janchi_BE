package com.springboot.janchi.banner.entity;

import com.springboot.janchi.janchi.entity.Janchi;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "banner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String banner1;
    private String banner2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "janchi_id")
    private Janchi janchi;
}

