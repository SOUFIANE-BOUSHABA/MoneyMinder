package com.example.moneyminder.VMs;

import com.example.moneyminder.entity.enums.ReportType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialReportVM {
    private Long id;
    private String title;
    private Date generationDate;
    private ReportType reportType;
    private String filePath;
    private Long userId;
}
