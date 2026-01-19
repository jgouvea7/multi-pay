package my.health_check.services;

import lombok.RequiredArgsConstructor;
import my.health_check.domain.entity.HealthCheck;
import my.health_check.domain.entity.HealthStatus;
import my.health_check.infrastructure.repository.HealthCheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class HealthCheckService {

    @Autowired
    private HealthCheckRepository healthCheckRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String PAGPAY = "PagPay";
    private static final String PAGPAY_HEALTH_URL =  "http://payment-pagpay:8001/v1/pagpay/health";

    private static final String PAGSAFE = "PagSafe";
    private static final String PAGSAFE_HEALTH_URL = "http://payment-pagsafe:8002/v1/pagsafe/health";


    private HealthCheck createInitial(String serviceName) {
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setServiceName(serviceName);
        healthCheck.setStatus(HealthStatus.DOWN);
        healthCheck.setErrorRate(BigDecimal.ZERO);
        return healthCheck;
    }

    private void updateHealth(String service, boolean success, long latency) {
        HealthCheck healthCheck = healthCheckRepository.findByServiceName(service).orElseGet(() -> createInitial(service));
        HealthStatus newStatus;

        if (!success) {
            newStatus = HealthStatus.DOWN;
        } else if (latency > 1500) {
            newStatus = HealthStatus.DEGRADED;
        } else {
            newStatus = HealthStatus.UP;
        }

        healthCheck.setStatus(newStatus);
        healthCheck.setP95(Math.max(latency, 0));
        healthCheck.setLastCheck(Instant.now());

        healthCheckRepository.save(healthCheck);
    }

    public void checkPagPay(){
        checkService(PAGPAY, PAGPAY_HEALTH_URL);
    }

    public void checkPagSafe() {
        checkService(PAGSAFE, PAGSAFE_HEALTH_URL);
    }

    private void checkService(String serviceName, String url) {
        long start = System.currentTimeMillis();

        boolean success;
        long latency;

        try {
            restTemplate.getForEntity(url, Void.class);
            latency = System.currentTimeMillis() - start;
            success = true;
        } catch (Exception e) {
            latency = 0;
            success = false;
        }

        updateHealth(serviceName, success, latency);
    }
}
