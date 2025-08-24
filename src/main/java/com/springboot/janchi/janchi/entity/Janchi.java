package com.springboot.janchi.janchi.entity;

import com.springboot.janchi.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "janchi",
        uniqueConstraints = @UniqueConstraint(name = "uk_janchi_name_start", columnNames = {"fstvl_nm", "start_date"}),
        indexes = {
                @Index(name = "idx_janchi_start_date", columnList = "start_date"),
                @Index(name = "idx_janchi_end_date", columnList = "end_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Janchi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fstvl_nm", length = 200)
    private String fstvlNm;

    @Column(name = "opar", length = 300)
    private String opar;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Lob
    @Column(name = "fstvl_co")
    private String fstvlCo;

    @Column(name = "mnnst_nm", length = 200)
    private String mnnstNm;

    @Column(name = "auspc_instt_nm", length = 200)
    private String auspcInsttNm;

    @Column(name = "suprt_instt_nm", length = 200)
    private String suprtInsttNm;

    @Column(name = "phone_number", length = 200)
    private String phoneNumber;

    @Column(name = "homepage_url", length = 500)
    private String homepageUrl;

    @Column(name = "relate_info", length = 500)
    private String relateInfo;

    @Column(name = "rdnmadr", length = 400)
    private String rdnmadr;

    @Column(name = "lnmadr", length = 400)
    private String lnmadr;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "reference_date")
    private LocalDate referenceDate;

    @Lob
    @Column(name = "image")
    private String image;


    @OneToMany(mappedBy = "janchi", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
}
