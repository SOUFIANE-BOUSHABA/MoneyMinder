package com.example.moneyminder.DTOs;

import com.example.moneyminder.entity.enums.ReportType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialReportRequest {
    private Date startDate;
    private Date endDate;
    private ReportType reportType;
}
