package my.health_check.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class HealthCheckScheduler {

    @Autowired
    private HealthCheckService healthCheckService;

    @Scheduled(fixedRate = 30000)
    public void runPagPayHealthCheck()
    {
        healthCheckService.checkPagPay();
    }

    @Scheduled(fixedRate = 30000)
    public void runPagSafeHealthCheck()
    {
        healthCheckService.checkPagSafe();
    }
}
