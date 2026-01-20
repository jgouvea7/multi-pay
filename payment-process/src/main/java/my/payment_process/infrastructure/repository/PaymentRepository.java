package my.payment_process.infrastructure.repository;

import my.payment_process.domain.entity.Payment;
import my.payment_process.domain.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByUsername(String username);
    Payment findByProcessId(UUID processId);
    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, Instant createdAt
    );
}
