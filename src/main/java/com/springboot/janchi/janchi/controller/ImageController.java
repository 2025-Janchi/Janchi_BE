package com.springboot.janchi.janchi.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    // 이미지가 실제로 저장된 서버의 외부 폴더 경로
    private final Path rootLocation = Paths.get("/home/ec2-user/app/images/");

    // "/images/파일명" 형태의 GET 요청을 처리
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // 요청된 파일 이름을 기반으로 실제 파일의 전체 경로를 만듭니다.
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

                // 파일 확장자에 따라 Content-Type을 동적으로 결정합니다.
                if (filename.toLowerCase().endsWith(".png")) {
                    contentType = MediaType.IMAGE_PNG_VALUE;
                } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
                    contentType = MediaType.IMAGE_JPEG_VALUE;
                } else if (filename.toLowerCase().endsWith(".gif")) {
                    contentType = MediaType.IMAGE_GIF_VALUE;
                }

                // HTTP 응답을 생성합니다.
                // Content-Type 헤더를 명확하게 지정하고, 파일 본문을 담아 보냅니다.
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                // 파일을 찾을 수 없는 경우 404 Not Found 응답을 보냅니다.
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            // URL 경로가 잘못된 경우 400 Bad Request 응답을 보냅니다.
            return ResponseEntity.badRequest().build();
        }
    }
}