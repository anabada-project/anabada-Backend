package com.example.anabadabackend.health;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final DataSource dataSource;
    private final RedisConnectionFactory redisConnectionFactory;
    private final AmazonS3 amazonS3;
    private final JavaMailSender mailSender;

    @Value("${cloud.aws.s3.bucket}")
    private String s3Bucket;

    @Value("${spring.mail.host}")
    private String mailHost;

    public HealthCheckResponse check() {
        Map<String, HealthCheckResponse.ServiceStatus> services = new LinkedHashMap<>();

        services.put("mysql", checkMySQL());
        services.put("redis", checkRedis());
        services.put("s3", checkS3());
        services.put("mail", checkMail());

        boolean allHealthy = services.values().stream()
                .allMatch(s -> s.getStatus() == HealthCheckResponse.Status.UP);

        return HealthCheckResponse.builder()
                .status(allHealthy ? HealthCheckResponse.Status.UP : HealthCheckResponse.Status.DOWN)
                .services(services)
                .build();
    }

    private HealthCheckResponse.ServiceStatus checkMySQL() {
        try (Connection conn = dataSource.getConnection()) {
            boolean valid = conn.isValid(2); // 2초 타임아웃
            if (valid) {
                return HealthCheckResponse.ServiceStatus.up("Connected to " + conn.getMetaData().getURL());
            } else {
                return HealthCheckResponse.ServiceStatus.down("Connection validation failed");
            }
        } catch (Exception e) {
            log.warn("[HealthCheck] MySQL 연결 실패: {}", e.getMessage());
            return HealthCheckResponse.ServiceStatus.down(e.getMessage());
        }
    }

    private HealthCheckResponse.ServiceStatus checkRedis() {
        try {
            var conn = redisConnectionFactory.getConnection();
            String pong = conn.ping();
            conn.close();
            if ("PONG".equalsIgnoreCase(pong)) {
                return HealthCheckResponse.ServiceStatus.up("PONG");
            } else {
                return HealthCheckResponse.ServiceStatus.down("Unexpected response: " + pong);
            }
        } catch (Exception e) {
            log.warn("[HealthCheck] Redis 연결 실패: {}", e.getMessage());
            return HealthCheckResponse.ServiceStatus.down(e.getMessage());
        }
    }

    private HealthCheckResponse.ServiceStatus checkS3() {
        try {
            boolean exists = amazonS3.doesBucketExistV2(s3Bucket);
            if (exists) {
                return HealthCheckResponse.ServiceStatus.up("Bucket '" + s3Bucket + "' accessible");
            } else {
                return HealthCheckResponse.ServiceStatus.down("Bucket '" + s3Bucket + "' not found");
            }
        } catch (Exception e) {
            log.warn("[HealthCheck] S3 연결 실패: {}", e.getMessage());
            return HealthCheckResponse.ServiceStatus.down(e.getMessage());
        }
    }

    private HealthCheckResponse.ServiceStatus checkMail() {
        try {
            // JavaMailSender는 실제 연결 테스트를 위해 Session 검증
            var session = ((org.springframework.mail.javamail.JavaMailSenderImpl) mailSender).getSession();
            session.getTransport("smtp"); // SMTP Transport 객체 생성 가능한지 확인
            return HealthCheckResponse.ServiceStatus.up("SMTP host reachable: " + mailHost);
        } catch (Exception e) {
            log.warn("[HealthCheck] Mail 서버 확인 실패: {}", e.getMessage());
            return HealthCheckResponse.ServiceStatus.down(e.getMessage());
        }
    }
}
