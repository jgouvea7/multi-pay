package my.payment_process.services;

import lombok.RequiredArgsConstructor;
import my.payment_process.domain.dto.HealthCheckResponse;
import my.payment_process.domain.dto.PaymentDto;
import my.payment_process.domain.entity.Payment;
import my.payment_process.domain.entity.PaymentStatus;
import my.payment_process.infrastructure.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HealtHttpClient healtHttpClient;



    /**
     * Processa o pagamento enviando os dados para um dos gateways disponíveis (PagPay ou Pagsafe).
     * Salva o pagamento inicialmente com status PENDING e tenta o envio com retries.
     * Retorna 200 em caso de sucesso ou 502 em caso de falha em ambos os gateways.
     */
    @Transactional
    public ResponseEntity<Void> processPayment(PaymentDto paymentDto) {

        PaymentDto savedDto = new PaymentDto();
        savedDto.setUsername(paymentDto.getUsername());
        savedDto.setMethods(paymentDto.getMethods());
        savedDto.setAmount(paymentDto.getAmount());
        savedDto.setStatus(PaymentStatus.PENDING);
        savedDto = savePayments(savedDto);

        HealthCheckResponse pagPayHealth = healtHttpClient.getHealthStatus("PagPay");
        HealthCheckResponse pagSafeHealth = healtHttpClient.getHealthStatus("PagSafe");

        String selectedService = selectBestService(pagPayHealth, pagSafeHealth);

        if (selectedService != null) {
            String url = selectedService.equals("PagPay")
                    ? "http://payment-pagpay:8001/v1/pagpay/"
                    : "http://payment-pagsafe:8002/v1/pagsafe/";

            if (tryProcess(url, savedDto)) {
                return ResponseEntity.ok().build();
            }

            String fallbackService = selectedService.equals("PagPay") ? "PagSafe" : "PagPay";
            String fallbackUrl = fallbackService.equals("PagPay")
                    ? "http://payment-pagpay:8001/v1/pagpay/"
                    : "http://payment-pagsafe:8002/v1/pagsafe/";

            HealthCheckResponse fallbackHealth = fallbackService.equals("PagPay") ? pagPayHealth : pagSafeHealth;

             if (isHealthy(fallbackHealth) && tryProcess(fallbackUrl, savedDto)) {
                 return ResponseEntity.ok().build();
             }
        }

        return ResponseEntity.status(503).build();
    }

    private boolean isHealthy(HealthCheckResponse health) {
        return "UP".equals(health.getStatus()) || "DEGRADED".equals(health.getStatus());
    }

    private String selectBestService(HealthCheckResponse pagPay, HealthCheckResponse pagSafe) {
        if (isHealthy(pagPay)) {
            return "PagPay";
        }

        if (isHealthy(pagSafe)) {
            return "PagSafe";
        }

        return null;
    }

    private boolean tryProcess(String url, PaymentDto dto) {
        int MAX_RETRY = 3;
        for (int retry = 1; retry <= MAX_RETRY; retry++) {
            try {
                restTemplate.postForEntity(url, dto, Void.class);
                return true;
            } catch (Exception e) {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        }
        return false;
    }

    /**
     * Salva os dados do pagamento no banco de dados com status atual.
     */
    public PaymentDto savePayments(PaymentDto paymentDto) {
        Payment payment = new Payment();
        payment.setUsername(paymentDto.getUsername());
        payment.setMethods(paymentDto.getMethods());
        payment.setAmount(paymentDto.getAmount());
        payment.setStatus(paymentDto.getStatus());

        Payment savedPayment = paymentRepository.save(payment);
        paymentDto.setProcessId(savedPayment.getProcessId());

        return paymentDto;
    }

    /**
     * Consome os dados enviados pelos gateways via Kafka e atualiza os dados do pagamento.
     */
    @KafkaListener(topics = "payment-topic", groupId = "payment-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumerPayment(PaymentDto paymentDto){
        Payment payment = new Payment();
        payment.setProcessId(paymentDto.getProcessId());
        payment.setUsername(paymentDto.getUsername());
        payment.setName(paymentDto.getName());
        payment.setMethods(paymentDto.getMethods());
        payment.setAmount(paymentDto.getAmount());
        payment.setTax(paymentDto.getTax());
        payment.setTotal(paymentDto.getTotal());
        payment.setStatus(paymentDto.getStatus());

        paymentRepository.save(payment);
    }

    /**
     * Retorna todos os pagamentos registrados.
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * Retorna todos os pagamentos de um usuário específico.
     */
    public List<Payment> getAllPaymentsForId(String username) {
        return paymentRepository.findAllByUsername(username);

    }

    /**
     * Deleta todos os registros de pagamento do banco.
     */
    public void deletAll() {
        paymentRepository.deleteAll();
    }
}
