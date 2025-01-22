package com.example.moneyminder.VMs;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsVM {
    private Map<String, Double> incomeExpense;
    private Map<String, Long> invoiceStatus;
    private Map<String, Long> quoteStatus;
    private Map<String, Map<String, Double>> monthlySummary;
}
