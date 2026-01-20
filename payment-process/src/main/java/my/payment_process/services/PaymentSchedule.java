package my.payment_process.services;

import my.payment_process.domain.entity.Payment;
import my.payment_process.domain.entity.PaymentStatus;
import my.payment_process.infrastructure.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@EnableScheduling
@Component
public class PaymentSchedule {

    @Autowired
    private PaymentRepository paymentRepository;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void cancelPendingPayments() {

        Instant timeout = Instant.now().minus(Duration.ofSeconds(15));

        List<Payment> pendings =
                paymentRepository.findByStatusAndCreatedAtBefore(
                        PaymentStatus.PENDING, timeout
                );

        for (Payment payment : pendings) {
            payment.setStatus(PaymentStatus.FAILURE);
        }
    }
}
