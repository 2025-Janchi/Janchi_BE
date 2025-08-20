package com.springboot.janchi.janchi.service;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.springboot.janchi.janchi.dto.JanchiMapDto;
import com.springboot.janchi.janchi.dto.JanchiResponse;
import com.springboot.janchi.janchi.dto.JanchiDetailDto;
import com.springboot.janchi.janchi.entity.Janchi;
import com.springboot.janchi.janchi.repository.JanchiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JanchiService {
    private final JanchiRepository janchiRepository;


    // yyyy-MM-dd, yyyy.MM.dd, yyyy/MM/dd, yyyy년 MM월 dd일, "yyyy-MM-dd ~ yyyy-MM-dd" 등 폭넓게 지원
    private static final Pattern DATE_ANY_PATTERN = Pattern.compile("(\\d{4})\\D?(\\d{1,2})\\D?(\\d{1,2})");

    public static LocalDate parseDateFirst(String v) {
        if (v == null || v.isBlank()) return null;
        Matcher m = DATE_ANY_PATTERN.matcher(v);
        if (m.find()) {
            try {
                int y = Integer.parseInt(m.group(1));
                int mo = Integer.parseInt(m.group(2));
                int d  = Integer.parseInt(m.group(3));
                return LocalDate.of(y, mo, d);
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static LocalDate parseDateLast(String v) {
        if (v == null || v.isBlank()) return null;
        Matcher m = DATE_ANY_PATTERN.matcher(v);
        LocalDate last = null;
        while (m.find()) {
            try {
                int y = Integer.parseInt(m.group(1));
                int mo = Integer.parseInt(m.group(2));
                int d  = Integer.parseInt(m.group(3));
                last = LocalDate.of(y, mo, d);
            } catch (Exception ignored) {}
        }
        return last;
    }

    private static LocalDate parseReference(String v) {
        return parseDateFirst(v);
    }

    private static Janchi toEntity(JanchiResponse dto) {
        LocalDate s = parseDateFirst(dto.getFstvlStartDate());
        LocalDate e = parseDateLast(dto.getFstvlEndDate());
        LocalDate ref = parseReference(dto.getReferenceDate());

        return Janchi.builder()
                .fstvlNm(dto.getFstvlNm())
                .opar(dto.getOpar())
                .startDate(s)
                .endDate(e)
                .fstvlCo(dto.getFstvlCo())
                .mnnstNm(dto.getMnnstNm())
                .auspcInsttNm(dto.getAuspcInsttNm())
                .suprtInsttNm(dto.getSuprtInsttNm())
                .phoneNumber(dto.getPhoneNumber())
                .homepageUrl(dto.getHomepageUrl())
                .relateInfo(dto.getRelateInfo())
                .rdnmadr(dto.getRdnmadr())
                .lnmadr(dto.getLnmadr())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .referenceDate(ref)
                .build();
    }

//    @Transactional
//    public int upsertAll(List<FestivalResponse> responses) {
//        if (responses == null || responses.isEmpty()) return 0;
//
//        List<Janchi> toSave = new ArrayList<>();
//
//        for (FestivalResponse fr : responses) {
//            LocalDate start = parseDateFirst(fr.getFstvlStartDate());
//            if (fr.getFstvlNm() == null || fr.getFstvlNm().isBlank() || start == null) {
//                continue;
//            }
//
//            Janchi entity = janchiRepository
//                    .findByFstvlNmAndStartDate(fr.getFstvlNm(), start)
//                    .map(exist -> {
//                        exist.setOpar(fr.getOpar());
//                        exist.setEndDate(parseDateLast(fr.getFstvlEndDate()));
//                        exist.setFstvlCo(fr.getFstvlCo());
//                        exist.setMnnstNm(fr.getMnnstNm());
//                        exist.setAuspcInsttNm(fr.getAuspcInsttNm());
//                        exist.setSuprtInsttNm(fr.getSuprtInsttNm());
//                        exist.setPhoneNumber(fr.getPhoneNumber());
//                        exist.setHomepageUrl(fr.getHomepageUrl());
//                        exist.setRelateInfo(fr.getRelateInfo());
//                        exist.setRdnmadr(fr.getRdnmadr());
//                        exist.setLnmadr(fr.getLnmadr());
//                        exist.setLatitude(fr.getLatitude());
//                        exist.setLongitude(fr.getLongitude());
//                        exist.setReferenceDate(parseReference(fr.getReferenceDate()));
//                        return exist;
//                    })
//                    .orElseGet(() -> toEntity(fr));
//
//            toSave.add(entity);
//        }
//
//        if (!toSave.isEmpty()) {
//            janchiRepository.saveAll(toSave);
//        }
//        return toSave.size();
//    }

    @Transactional
    public int upsertAll(List<JanchiResponse> responses) {
        if (responses == null || responses.isEmpty()) return 0;

        List<Janchi> toSave = new ArrayList<>();

        for (JanchiResponse fr : responses) {
            LocalDate start = parseDateFirst(fr.getFstvlStartDate());
            if (fr.getFstvlNm() == null || fr.getFstvlNm().isBlank() || start == null) {
                continue;
            }

            Janchi entity = janchiRepository
                    .findByFstvlNmAndStartDate(fr.getFstvlNm(), start)
                    .map(exist -> {
                        exist.setOpar(fr.getOpar());
                        exist.setEndDate(parseDateLast(fr.getFstvlEndDate()));
                        exist.setFstvlCo(fr.getFstvlCo());
                        exist.setMnnstNm(fr.getMnnstNm());
                        exist.setAuspcInsttNm(fr.getAuspcInsttNm());
                        exist.setSuprtInsttNm(fr.getSuprtInsttNm());
                        exist.setPhoneNumber(fr.getPhoneNumber());
                        exist.setHomepageUrl(fr.getHomepageUrl());
                        exist.setRelateInfo(fr.getRelateInfo());
                        exist.setRdnmadr(fr.getRdnmadr());
                        exist.setLnmadr(fr.getLnmadr());
                        exist.setLatitude(fr.getLatitude());
                        exist.setLongitude(fr.getLongitude());
                        exist.setReferenceDate(parseReference(fr.getReferenceDate()));
                        return exist;
                    })
                    .orElseGet(() -> toEntity(fr));

            toSave.add(entity);
        }

        if (!toSave.isEmpty()) {
            janchiRepository.saveAll(toSave);
        }
        return toSave.size();
    }

    /** 저장/업데이트 후 DTO에 DB id를 채워서 돌려주는 별도 메서드 */
    @Transactional
    public List<JanchiResponse> upsertAllAndAttachIds(List<JanchiResponse> responses) {
        // 1) 저장/업데이트
        upsertAll(responses);

        // 2) 각 DTO에 id 매핑 (키: 축제명 + 시작일)
        for (JanchiResponse fr : responses) {
            LocalDate start = parseDateFirst(fr.getFstvlStartDate());
            if (fr.getFstvlNm() == null || start == null) continue;

            janchiRepository.findByFstvlNmAndStartDate(fr.getFstvlNm(), start)
                    .ifPresent(j -> fr.setId(j.getId()));
        }
        return responses;
    }


    @Transactional(readOnly = true)
    public JanchiDetailDto getDetail(Long id) {
        Janchi f = janchiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("축제를 찾을 수 없습니다. id=" + id));

        // 파생값 계산
        var tz = ZoneId.of("Asia/Seoul");
        var today = java.time.LocalDate.now(tz);
        var s = f.getStartDate();
        var e = f.getEndDate() != null ? f.getEndDate() : f.getStartDate();

        boolean ongoing = false;
        Integer dday = null;
        Integer duration = null;

        if (s != null) {
            if (e == null) e = s;
            ongoing = !s.isAfter(today) && !e.isBefore(today);  // s ≤ today ≤ e
            if (ongoing) {
                dday = 0;
            } else if (today.isBefore(s)) {
                dday = (int) (s.toEpochDay() - today.toEpochDay());
            }
            duration = (int) (e.toEpochDay() - s.toEpochDay()) + 1;
        }

        return JanchiDetailDto.builder()
                .id(f.getId())
                .fstvlNm(f.getFstvlNm())
                .opar(f.getOpar())
                .startDate(f.getStartDate())
                .endDate(f.getEndDate())
                .fstvlCo(f.getFstvlCo())
                .mnnstNm(f.getMnnstNm())
                .auspcInsttNm(f.getAuspcInsttNm())
                .suprtInsttNm(f.getSuprtInsttNm())
                .phoneNumber(f.getPhoneNumber())
                .homepageUrl(f.getHomepageUrl())
                .relateInfo(f.getRelateInfo())
                .rdnmadr(f.getRdnmadr())
                .lnmadr(f.getLnmadr())
                .latitude(f.getLatitude())
                .longitude(f.getLongitude())
                .referenceDate(f.getReferenceDate())
                .ongoing(ongoing)
                .dday(dday)
                .duration(duration)
                .build();
    }

}
