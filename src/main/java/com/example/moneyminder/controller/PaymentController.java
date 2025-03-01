package com.example.moneyminder.controller;

import com.example.moneyminder.DTOs.PaymentRequest;
import com.example.moneyminder.VMs.PaymentVM;
import com.example.moneyminder.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentVM> createPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<PaymentVM>> getPaymentsForInvoice(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(paymentService.getPaymentsForInvoice(invoiceId));
    }

    @GetMapping("/quote/{quoteId}")
    public ResponseEntity<List<PaymentVM>> getPaymentsForQuote(@PathVariable Long quoteId) {
        return ResponseEntity.ok(paymentService.getPaymentsForQuote(quoteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentVM> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentVM> updatePayment(@PathVariable Long id, @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.updatePayment(id, request));
    }


    @GetMapping("/user")
    public ResponseEntity<List<PaymentVM>> getPaymentsForCurrentUser() {
        return ResponseEntity.ok(paymentService.getAllPaymentsForUser());
    }
}
