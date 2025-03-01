package com.example.moneyminder.service;

import com.example.moneyminder.DTOs.PaymentRequest;
import com.example.moneyminder.VMs.PaymentVM;

import java.util.List;

public interface PaymentService {
    PaymentVM createPayment(PaymentRequest request);

    PaymentVM updatePayment(Long id, PaymentRequest request);

    List<PaymentVM> getPaymentsForInvoice(Long invoiceId);

    List<PaymentVM> getPaymentsForQuote(Long quoteId);

    PaymentVM getPaymentById(Long id);

    void deletePayment(Long id);

    List<PaymentVM> getAllPaymentsForUser();
}
