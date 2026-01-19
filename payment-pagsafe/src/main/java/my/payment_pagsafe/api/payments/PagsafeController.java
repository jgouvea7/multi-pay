package my.payment_pagsafe.api.payments;

import my.payment_pagsafe.domain.dto.PaymentDto;
import my.payment_pagsafe.domain.entity.Pagsafe;
import my.payment_pagsafe.services.PagsafeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/pagsafe")
public class PagsafeController {

    @Autowired
    private PagsafeService pagsafeService;

    @GetMapping("/health")
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/")
    public ResponseEntity<Void> payments(@RequestBody PaymentDto paymentDto) {
        pagsafeService.payment(paymentDto);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<Pagsafe>> getAllPayments() {
        List<Pagsafe> allPayments = pagsafeService.getAllPayments();
        return ResponseEntity.ok(allPayments);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteAll() {
        pagsafeService.deletAll();
        return ResponseEntity.noContent().build();
    }
}
