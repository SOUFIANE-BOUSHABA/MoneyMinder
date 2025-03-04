package com.example.moneyminder.entity;

import com.example.moneyminder.entity.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "financial_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date generationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @Column(nullable = false)
    private String filePath;

    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
