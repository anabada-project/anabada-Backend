package com.example.anabadabackend.global.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.anabadabackend.global.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public PresignedUrlResponse generatePresignedUrl(String filename, String contentType) {

        String safeFilename = sanitizeFilename(filename);
        String key = "products/" + UUID.randomUUID() + "_" + safeFilename;

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5);

        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);

        request.setContentType(contentType);

        String uploadUrl = amazonS3.generatePresignedUrl(request).toString();
        String imageUrl = amazonS3.getUrl(bucket, key).toString();

        return new PresignedUrlResponse(uploadUrl, imageUrl);
    }


    public List<PresignedUrlResponse> generatePresignedUrls(List<String> filenames, List<String> contentTypes) {

        if (filenames == null || contentTypes == null) {
            throw new IllegalArgumentException("filenames or contentTypes is null");
        }

        if (filenames.size() != contentTypes.size()) {
            throw new IllegalArgumentException("filenames and contentTypes size mismatch");
        }

        List<PresignedUrlResponse> result = new ArrayList<>();

        for (int i = 0; i < filenames.size(); i++) {
            result.add(generatePresignedUrl(filenames.get(i), contentTypes.get(i)));
        }

        return result;
    }


    public void deleteImage(String imageUrl) {

        String fileName = extractFileName(imageUrl);
        if (fileName == null) return;

        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
            log.info("[S3 이미지 삭제 성공] fileName={}", fileName);

        } catch (Exception e) {
            log.error("[S3 삭제 실패] imageUrl={}, error={}", imageUrl, e.getMessage());
        }
    }

    public void deleteImages(List<String> imageUrls) {

        if (imageUrls == null || imageUrls.isEmpty()) return;

        for (String url : imageUrls) {
            deleteImage(url);
        }
    }


    private String extractFileName(String imageUrl) {

        if (imageUrl == null) return null;

        int index = imageUrl.indexOf("products/");
        if (index == -1) return null;

        return imageUrl.substring(index);
    }


    private String sanitizeFilename(String filename) {

        if (filename == null) return "unknown";

        return filename.replaceAll("[^a-zA-Z0-9.\\-]", "_");
    }
}