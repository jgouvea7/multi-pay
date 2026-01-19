package my.payment_pagpay.api;

import my.payment_pagpay.domain.dto.PaymentDto;
import my.payment_pagpay.domain.entity.Pagpay;
import my.payment_pagpay.services.PagpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/pagpay")
public class PagpayController {

    @Autowired
    private PagpayService pagpayService;

    @GetMapping("/health")
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/")
    public ResponseEntity<Void> payments(@RequestBody PaymentDto paymentDto) {
        pagpayService.payment(paymentDto);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<Pagpay>> getAllPayments() {
        List<Pagpay> allPayments = pagpayService.getAllPayments();
        return ResponseEntity.ok(allPayments);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteAll() {
        pagpayService.deletAll();
        return ResponseEntity.noContent().build();
    }
}
