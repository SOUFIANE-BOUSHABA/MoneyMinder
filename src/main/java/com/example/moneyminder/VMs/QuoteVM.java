package com.example.moneyminder.VMs;

import com.example.moneyminder.entity.enums.QuoteStatus;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteVM {
    private Long id;
    private String quoteNumber;
    private Date issueDate;
    private Double totalAmount;
    private QuoteStatus status;
    private Long userId;
}