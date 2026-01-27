package com.sonardemo.controller;

import com.sonardemo.model.Payment;
import com.sonardemo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> processPayment(@RequestBody Payment payment) {
        Payment result = paymentService.processPayment(payment);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(@PathVariable String paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Payment>> getCustomerPayments(@PathVariable String customerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerId));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<String> refundPayment(
            @PathVariable String paymentId,
            @RequestParam BigDecimal amount) {
        if (paymentService.refundPayment(paymentId, amount)) {
            return ResponseEntity.ok("Refund processed");
        }
        return ResponseEntity.badRequest().body("Refund failed");
    }

    @PostMapping("/reconcile")
    public ResponseEntity<Void> reconcilePayments(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        paymentService.reconcilePayments(startDate, endDate);
        return ResponseEntity.ok().build();
    }
}

