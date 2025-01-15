package com.example.moneyminder.DTOs;

import com.example.moneyminder.entity.enums.QuoteStatus;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteRequest {
    private String quoteNumber;
    private Date issueDate;
    private Double totalAmount;
    private QuoteStatus status;
}