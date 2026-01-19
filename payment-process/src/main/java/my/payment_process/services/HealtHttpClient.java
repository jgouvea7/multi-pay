package my.payment_process.services;

import lombok.RequiredArgsConstructor;
import my.payment_process.domain.dto.HealthCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HealtHttpClient {

    @Autowired
    private RestTemplate restTemplate;

    public HealthCheckResponse getHealthStatus(String service) {
        try {
            ResponseEntity<HealthCheckResponse> response =  restTemplate.getForEntity("http://health-check:8003/v1/health/" + service, HealthCheckResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            HealthCheckResponse healthCheckResponse = new HealthCheckResponse();
            healthCheckResponse.setStatus("DOWN");
            return healthCheckResponse;
        }
    }
}
