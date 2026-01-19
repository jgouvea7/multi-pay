package my.payment_pagsafe.services;

import lombok.RequiredArgsConstructor;
import my.payment_pagsafe.domain.dto.PaymentDto;
import my.payment_pagsafe.domain.entity.Pagsafe;
import my.payment_pagsafe.domain.entity.PagsafeStatus;
import my.payment_pagsafe.infrastructure.kafka.KafkaProducer;
import my.payment_pagsafe.infrastructure.repository.PagsafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PagsafeService {

    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private PagsafeRepository pagsafeRepository;


    /**
     * Processa o pagamento via PagSafe, calcula a taxa e envia os dados para o Kafka.
     */
    public Pagsafe payment(PaymentDto paymentDto) {
        if (paymentDto.getProcessId() == null) {
            throw new IllegalArgumentException("ProcessId não pode ser nulo");
        }

        // Calcula a taxa fixa de 6%
        double taxD = 0.06;
        BigDecimal taxBD = BigDecimal.valueOf(taxD);
        BigDecimal tax = paymentDto.getAmount()
                .multiply(taxBD)
                .setScale(2, RoundingMode.HALF_UP);
        // Total final apos cobrança da taxa
        BigDecimal total = paymentDto.getAmount()
                .subtract(tax)
                .setScale(2, RoundingMode.HALF_UP);

        // Cria entidade para salvar no banco
        Pagsafe pagsafe = new Pagsafe();
        pagsafe.setProcessId(paymentDto.getProcessId());
        pagsafe.setUsername(paymentDto.getUsername());
        pagsafe.setName("PagSafe");
        pagsafe.setMethods(paymentDto.getMethods());
        pagsafe.setAmount(paymentDto.getAmount());
        pagsafe.setTax(tax);
        pagsafe.setTotal(total);
        pagsafe.setStatus(PagsafeStatus.SUCCESS);

        // Cria dto para enviar ao Kafka
        PaymentDto dto = new PaymentDto();
        dto.setProcessId(pagsafe.getProcessId());
        dto.setUsername(pagsafe.getUsername());
        dto.setName(pagsafe.getName());
        dto.setMethods(pagsafe.getMethods());
        dto.setAmount(pagsafe.getAmount());
        dto.setTax(pagsafe.getTax());
        dto.setTotal(pagsafe.getTotal());
        dto.setStatus(PagsafeStatus.SUCCESS);

        kafkaProducer.paymentProducer(dto);
        return pagsafeRepository.save(pagsafe);
    }

    /**
    * Retorna todos os pagamentos processados pelo PagSafe.
    */
    public List<Pagsafe> getAllPayments() {
        List<Pagsafe> allPayments = pagsafeRepository.findAll();
        return allPayments;
    }

    /**
     * Remove todos os registros de pagamento do banco de dados.
     */
    public void deletAll() {
        pagsafeRepository.deleteAll();
    }
}
