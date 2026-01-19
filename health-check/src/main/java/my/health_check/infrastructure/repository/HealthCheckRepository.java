package my.health_check.infrastructure.repository;

import my.health_check.domain.entity.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, UUID> {
    Optional<HealthCheck> findByServiceName(String serviceName);
}
