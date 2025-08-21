package com.springboot.janchi.stay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springboot.janchi.stay.entity.Stay;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class StayResponseDto {

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StayResponse {
        private InnerResponse response;

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class InnerResponse {
            private Header header;
            private Body body;
        }

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Header {
            private String resultCode;
            private String resultMsg;
        }

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Body {
            private Items items;
            private Integer numOfRows;
            private Integer pageNo;
            private Integer totalCount;
        }

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Items {
            @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            private List<StayItem> item;
        }

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class StayItem {
            private String firstimage;   // 원본이미지
            private String firstimage2;  // 썸네일 150x100
            private String addr1;
            private String addr2;
            private String contentid;
            private String contenttypeid;
            private String tel;
            private String title;

            @JsonProperty("areacode")
            private String areaCode;
            private String sigunguCode;

            private String lDongRegnCd;
            private String lDongSignguCd;

            private String mapx;
            private String mapy;
        }
    }
    @Data
    @NoArgsConstructor
    public static class StayDetailResponseDto {
        private String contentid;
        private String name;
        private String addr1;
        private String addr2;
        private String tel;
        private String firstimage;
        private String firstimage2;

        public StayDetailResponseDto(Stay stay) {
            this.contentid = stay.getContentid();
            this.name = stay.getTitle();
            this.addr1 = stay.getAddr1();
            this.addr2 = stay.getAddr2();
            this.tel = stay.getTel();
            this.firstimage = stay.getFirstimage();
            this.firstimage2 = stay.getFirstimage2();
        }
    }

}
