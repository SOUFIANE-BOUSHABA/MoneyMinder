package com.example.moneyminder.VMs;

import com.example.moneyminder.entity.enums.PaymentMethod;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentVM {
    private Long id;
    private Double amount;
    private Date paymentDate;
    private PaymentMethod method;
    private String invoiceNumber;
    private String quoteNumber;
    private String statusMessage;
}
