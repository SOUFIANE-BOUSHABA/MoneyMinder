package com.example.moneyminder.DTOs;

import com.example.moneyminder.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentRequest {
    @NotNull
    private Double amount;

    @NotNull
    private Date paymentDate;

    @NotNull
    private PaymentMethod method;

    private Long invoiceId;
    private Long quoteId;
}
