package my.payment_process.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HealthCheckResponse {
    private String status;
    private String serviceName;
    private Long p95;
    private BigDecimal errorRate;
}
