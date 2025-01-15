package com.example.moneyminder.DTOs;

import com.example.moneyminder.entity.enums.InvoiceStatus;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceRequest {
    private String invoiceNumber;
    private Date issueDate;
    private Double totalAmount;
    private InvoiceStatus status;
}