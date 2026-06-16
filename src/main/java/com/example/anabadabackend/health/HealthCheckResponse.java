package com.example.anabadabackend.health;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class HealthCheckResponse {

    private Status status;
    private Map<String, ServiceStatus> services;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public boolean isHealthy() {
        return status == Status.UP;
    }

    public enum Status {
        UP, DOWN
    }

    @Getter
    @Builder
    public static class ServiceStatus {
        private Status status;
        private String detail;

        public static ServiceStatus up(String detail) {
            return ServiceStatus.builder()
                    .status(Status.UP)
                    .detail(detail)
                    .build();
        }

        public static ServiceStatus down(String detail) {
            return ServiceStatus.builder()
                    .status(Status.DOWN)
                    .detail(detail)
                    .build();
        }
    }
}
