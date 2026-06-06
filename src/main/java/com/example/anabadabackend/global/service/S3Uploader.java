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


    /**
     * Presigned URL 단건 발급
     */
    public PresignedUrlResponse generatePresignedUrl(String filename, String contentType) {
        String key = "products/" + UUID.randomUUID() + "_" + filename;

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5); // 5분

        GeneratePresignedUrlRequest presignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);
        presignedUrlRequest.setContentType(contentType);

        String uploadUrl = amazonS3.generatePresignedUrl(presignedUrlRequest).toString();
        String imageUrl = amazonS3.getUrl(bucket, key).toString();

        return new PresignedUrlResponse(uploadUrl, imageUrl);
    }

    /**
     * Presigned URL 여러 장 발급
     */
    public List<PresignedUrlResponse> generatePresignedUrls(List<String> filenames, List<String> contentTypes) {
        List<PresignedUrlResponse> result = new ArrayList<>();
        for (int i = 0; i < filenames.size(); i++) {
            result.add(generatePresignedUrl(filenames.get(i), contentTypes.get(i)));
        }
        return result;
    }

    /**
     * 이미지 삭제
     */
    public void deleteImage(String imageUrl) {
        try {
            String fileName = imageUrl.substring(imageUrl.indexOf("products/"));
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
            log.info("[S3 이미지 삭제] fileName={}", fileName);
        } catch (Exception e) {
            log.error("[S3 삭제 실패] imageUrl={}, error={}", imageUrl, e.getMessage());
        }
    }

    /**
     * 이미지 여러 장 삭제
     */
    public void deleteImages(List<String> imageUrls) {
        for (String url : imageUrls) {
            deleteImage(url);
        }
    }
}