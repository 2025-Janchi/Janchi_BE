package com.springboot.janchi.banner.controller;

import com.springboot.janchi.banner.dto.BannerResponseDto;
import com.springboot.janchi.banner.service.BannerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping("/save/{janchiId}")
    public ResponseEntity<BannerResponseDto> getBannerById(@PathVariable Long janchiId) {
        try {
            BannerResponseDto banner = bannerService.getRandomBanner(janchiId);
            return ResponseEntity.ok(banner);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/view/{janchiId}")
    public ResponseEntity<BannerResponseDto> getSavedBanner(@PathVariable Long janchiId) {
        try {
            return ResponseEntity.ok(bannerService.getSavedBanner(janchiId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}