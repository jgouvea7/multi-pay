package my.health_check.domain.dto;

import lombok.Getter;
import lombok.Setter;
import my.health_check.domain.entity.HealthStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class HealthCheckDto {
    private HealthStatus status;
    private String serviceName;
    private Long p95;
    private BigDecimal errorRate;
    private Instant lastCheck;
}
