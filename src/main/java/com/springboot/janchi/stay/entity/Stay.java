package com.springboot.janchi.stay.entity;

import com.springboot.janchi.area.dto.AreaResponseDto;
import com.springboot.janchi.area.entity.Area;
import com.springboot.janchi.stay.dto.StayResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "stay")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stay {

    @Id
    @Column(name = "content_id", length = 50)
    private String contentid;
    @Column(name = "content_type_id", length = 10)
    private String contentTypeId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "addr1", length = 300)
    private String addr1;

    @Column(name = "addr2", length = 300)
    private String addr2;

    // 지역 코드
    @Column(name = "area_code", length = 10)
    private String areaCode;

    // 이미지 원본
    @Column(name = "image_url")
    private String firstimage;

    // 썸네일
    @Column(name = "image_thumb_url")
    private String firstimage2;

    // 좌표
    @Column(name = "map_x", precision = 15, scale = 8)
    private String mapX;

    @Column(name = "map_y", precision = 15, scale = 8)
    private String mapY;

    @Column(name = "tel", length = 100)
    private String tel;

    public static Stay fromStayItem(StayResponseDto.StayResponse.StayItem i) {
        return Stay.builder()
                .contentid(i.getContentid())
                .contentTypeId(i.getContenttypeid())
                .title(i.getTitle())
                .addr1(i.getAddr1())
                .addr2(i.getAddr2())
                .areaCode(i.getAreaCode())
                //.sigunguCode(i.getSigungucode())
                .firstimage(i.getFirstimage())       // 원본
                .firstimage2(i.getFirstimage2()) // 썸네일
                .mapX(i.getMapx())
                .mapY(i.getMapy())
                .tel(i.getTel())
                .build();
    }
}
