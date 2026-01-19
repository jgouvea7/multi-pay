package my.health_check.api.health_check;

import my.health_check.domain.dto.HealthCheckDto;
import my.health_check.domain.entity.HealthCheck;
import my.health_check.infrastructure.repository.HealthCheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/health")
public class HealthCheckController {

    @Autowired
    private HealthCheckRepository healthCheckRepository;


    @GetMapping("/{service}")
    public ResponseEntity<HealthCheckDto> getHealthCheck(@PathVariable String service) {
        HealthCheck healthCheck = healthCheckRepository
                .findByServiceName(service)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Service not found"
                ));

        HealthCheckDto response = new HealthCheckDto();
        response.setServiceName(service);
        response.setStatus(healthCheck.getStatus());
        response.setP95(healthCheck.getP95());
        response.setLastCheck(healthCheck.getLastCheck());

        return ResponseEntity.ok(response);
    }
}
