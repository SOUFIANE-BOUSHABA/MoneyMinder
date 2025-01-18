package com.example.moneyminder.VMs;

import com.example.moneyminder.entity.enums.InvoiceStatus;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceVM {
    private Long id;
    private String invoiceNumber;
    private Date issueDate;
    private Double totalAmount;
    private InvoiceStatus status;
    private Long userId;
    private Double paymentPercentage;
}