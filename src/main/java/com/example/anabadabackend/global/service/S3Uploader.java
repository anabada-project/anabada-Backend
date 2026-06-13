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


    public List<PresignedUrlResponse> generatePresignedUrls(List<String> filenames, List<String> contentTypes) {
        List<PresignedUrlResponse> result = new ArrayList<>();
        for (int i = 0; i < filenames.size(); i++) {
            result.add(generatePresignedUrl(filenames.get(i), contentTypes.get(i)));
        }
        return result;
    }


    public void deleteImage(String imageUrl) {

        if (imageUrl == null || !imageUrl.contains("products/")) {
            return;
        }

        String fileName = imageUrl.substring(imageUrl.indexOf("products/"));
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }


    public void deleteImages(List<String> imageUrls) {
        for (String url : imageUrls) {
            deleteImage(url);
        }
    }
}