package com.springboot.janchi.area.entity;

import com.springboot.janchi.area.dto.AreaResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "area")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Area {

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

//    // 시군구 코드
//    @Column(name = "sigungu_code", length = 10)
//    private String sigunguCode;

    // 이미지 원본
    @Column(name = "image_url")
    private String imageUrl;

     // 썸네일
    @Column(name = "image_thumb_url")
    private String imageThumbUrl;

    // 좌표
    @Column(name = "map_x", precision = 15, scale = 8)
    private String mapX;

    @Column(name = "map_y", precision = 15, scale = 8)
    private String mapY;

    @Column(name = "tel", length = 100)
    private String tel;


//    public static Area fromDto (AreaRequestDto dto){
//        return Area.builder()
//                .contentid(dto.getContentid())
//
//    }

    public static Area fromAreaItem(AreaResponseDto.AreaResponse.AreaItem i) {
        return Area.builder()
                .contentid(i.getContentid())
                .contentTypeId(i.getContenttypeid())
                .title(i.getTitle())
                .addr1(i.getAddr1())
                .addr2(i.getAddr2())
                .areaCode(i.getAreaCode())
                //.sigunguCode(i.getSigungucode())
                .imageUrl(i.getFirstimage())       // 원본
                .imageThumbUrl(i.getFirstimage2()) // 썸네일
                .mapX(i.getMapx())
                .mapY(i.getMapy())
                .tel(i.getTel())
                .build();
    }

}
