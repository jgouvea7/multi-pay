package my.health_check.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class HealthCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String serviceName;
    @Enumerated(EnumType.STRING)
    private HealthStatus status;
    private Long p95;
    @Column(precision = 5, scale = 2)
    private BigDecimal errorRate;
    private Instant lastCheck;
}
