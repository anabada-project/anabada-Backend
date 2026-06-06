package com.example.anabadabackend.global.global.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    static {
        System.setProperty("aws.java.v1.disableDeprecationAnnouncement", "true");
    }

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials credentials =
                new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder.standard()
                .withRegion("ap-southeast-2")  // 하드코딩
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}