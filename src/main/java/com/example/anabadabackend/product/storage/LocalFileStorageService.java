package com.example.anabadabackend.product.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 로컬 파일 저장 서비스
 * 추후 S3로 교체할 경우 이 클래스만 교체하면 됩니다.
 * (인터페이스 분리를 원하면 FileStorageService 인터페이스 추출 후 구현체로 전환)
 */
@Service
public class LocalFileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.base-url:/uploads}")
    private String baseUrl;

    /**
     * 파일을 로컬에 저장하고 접근 가능한 URL 반환
     *
     * @param file 업로드된 파일
     * @return 저장된 파일의 URL (예: /uploads/uuid.jpg)
     */
    public String store(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String savedFilename = UUID.randomUUID() + "." + extension;

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path targetPath = uploadPath.resolve(savedFilename);
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다: " + originalFilename, e);
        }

        return baseUrl + "/" + savedFilename;
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
