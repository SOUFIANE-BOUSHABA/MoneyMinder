package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.PaymentRequest;
import com.example.moneyminder.VMs.PaymentVM;
import com.example.moneyminder.entity.Invoice;
import com.example.moneyminder.entity.Payment;
import com.example.moneyminder.entity.Quote;
import com.example.moneyminder.entity.enums.InvoiceStatus;
import com.example.moneyminder.entity.enums.QuoteStatus;
import com.example.moneyminder.exception.CustomException;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.PaymentMapper;
import com.example.moneyminder.repository.InvoiceRepository;
import com.example.moneyminder.repository.PaymentRepository;
import com.example.moneyminder.repository.QuoteRepository;
import com.example.moneyminder.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final QuoteRepository quoteRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentVM createPayment(PaymentRequest request) {
        Payment payment = paymentMapper.toEntity(request);

        if (request.getInvoiceId() != null && request.getQuoteId() != null) {
            throw new CustomException("Payment cannot be associated with both an invoice and a quote.");
        }

        String statusMessage;

        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + request.getInvoiceId()));

            double totalPaid = invoice.getPayments().stream().mapToDouble(Payment::getAmount).sum();
            double remainingAmount = invoice.getTotalAmount() - totalPaid;

            if (request.getAmount() > remainingAmount) {
                throw new CustomException("Payment amount exceeds the remaining invoice balance.");
            }

            payment.setInvoice(invoice);

            if (request.getAmount() == remainingAmount) {
                invoice.setStatus(InvoiceStatus.PAID);
                statusMessage = "Invoice fully paid.";
            } else {
                invoice.setStatus(InvoiceStatus.PENDING);
                statusMessage = "Partial payment made for invoice.";
            }
            invoiceRepository.save(invoice);

            payment.setAmount(request.getAmount());
        } else if (request.getQuoteId() != null) {
            Quote quote = quoteRepository.findById(request.getQuoteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quote not found with ID: " + request.getQuoteId()));

            double totalPaid = quote.getPayments().stream().mapToDouble(Payment::getAmount).sum();
            double remainingAmount = quote.getTotalAmount() - totalPaid;

            if (request.getAmount() > remainingAmount) {
                throw new CustomException("Payment amount exceeds the remaining quote balance.");
            }

            payment.setQuote(quote);

            if (request.getAmount() == remainingAmount) {
                quote.setStatus(QuoteStatus.ACCEPTED);
                statusMessage = "Quote fully paid and accepted.";
            } else {
                quote.setStatus(QuoteStatus.DRAFT);
                statusMessage = "Partial payment made for quote.";
            }
            quoteRepository.save(quote);

            payment.setAmount(request.getAmount());
        } else {
            throw new CustomException("Payment must be associated with either an invoice or a quote.");
        }

        Payment savedPayment = paymentRepository.save(payment);
        PaymentVM paymentVM = paymentMapper.toVM(savedPayment);
        paymentVM.setStatusMessage(statusMessage);

        return paymentVM;
    }

    @Override
    public List<PaymentVM> getPaymentsForInvoice(Long invoiceId) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getInvoice() != null && payment.getInvoice().getId().equals(invoiceId))
                .map(paymentMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentVM> getPaymentsForQuote(Long quoteId) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getQuote() != null && payment.getQuote().getId().equals(quoteId))
                .map(paymentMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentVM getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return paymentMapper.toVM(payment);
    }

    @Override
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));

        if (payment.getInvoice() != null) {
            Invoice invoice = payment.getInvoice();

            double totalPaid = invoice.getPayments().stream()
                    .filter(p -> !p.getId().equals(payment.getId()))
                    .mapToDouble(Payment::getAmount).sum();

            if (totalPaid == 0) {
                invoice.setStatus(InvoiceStatus.PENDING);
            } else if (totalPaid < invoice.getTotalAmount()) {
                invoice.setStatus(InvoiceStatus.PENDING);
            } else {
                invoice.setStatus(InvoiceStatus.PAID);
            }

            invoiceRepository.save(invoice);
        }

        if (payment.getQuote() != null) {
            Quote quote = payment.getQuote();

            double totalPaid = quote.getPayments().stream()
                    .filter(p -> !p.getId().equals(payment.getId()))
                    .mapToDouble(Payment::getAmount).sum();

            if (totalPaid == 0) {
                quote.setStatus(QuoteStatus.DRAFT);
            } else if (totalPaid < quote.getTotalAmount()) {
                quote.setStatus(QuoteStatus.DRAFT);
            } else {
                quote.setStatus(QuoteStatus.ACCEPTED);
            }

            quoteRepository.save(quote);
        }

        paymentRepository.delete(payment);
    }
}
