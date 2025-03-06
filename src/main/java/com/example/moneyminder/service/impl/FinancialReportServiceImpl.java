package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.FinancialReportRequest;
import com.example.moneyminder.VMs.FinancialReportVM;
import com.example.moneyminder.entity.*;
import com.example.moneyminder.entity.enums.ReportType;
import com.example.moneyminder.entity.enums.SubscriptionStatus;
import com.example.moneyminder.exception.CustomException;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.FinancialReportMapper;
import com.example.moneyminder.repository.*;
import com.example.moneyminder.service.FinancialReportService;
import com.example.moneyminder.utils.PdfGenerator;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialReportServiceImpl implements FinancialReportService {

    private final FinancialReportRepository reportRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final QuoteRepository quoteRepository;
    private final TransactionRepository transactionRepository;
    private final FinancialReportMapper reportMapper;
    private final EmailService emailService;
    private final SubscriptionRepository subscriptionRepository;




    @Override
    public FinancialReportVM generateFinancialReport(FinancialReportRequest request) {
        User currentUser = getCurrentUser();


        boolean isPremium = isUserPremium(currentUser.getId());

        if (!isPremium) {
            ensureFreePlanUnderLimit(currentUser.getId());
        }


        List<Transaction> transactions = transactionRepository.findAllByUserIdAndDateBetween(  currentUser.getId(), request.getStartDate(), request.getEndDate());
        List<Invoice> invoices = invoiceRepository.findAllByUser_IdAndIssueDateBetween(         currentUser.getId(), request.getStartDate(), request.getEndDate());
        List<Quote> quotes = quoteRepository.findAllByUser_IdAndIssueDateBetween(               currentUser.getId(), request.getStartDate(), request.getEndDate());


        String reportTitle = request.getReportType() + " Financial Report";
        byte[] pdfContent = PdfGenerator.generateFinancialReportPdf(transactions, invoices, quotes, request);


        String filePath = saveReportFile(pdfContent, reportTitle);
        long fileSize = (long) pdfContent.length;

        FinancialReport report = FinancialReport.builder()
                .title(reportTitle)
                .generationDate(new Date())
                .reportType(request.getReportType())
                .filePath(filePath)
                .fileSize(fileSize)
                .user(currentUser)
                .build();


        sendReportEmail(currentUser.getEmail(), reportTitle, pdfContent);

        return reportMapper.toVM(reportRepository.save(report));
    }




    private void ensureFreePlanUnderLimit(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        Date startDate = Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        List<FinancialReport> thisMonthReports = reportRepository.findAllByUserIdAndGenerationDateBetween(userId, startDate, endDate);
        if (thisMonthReports.size() >= 3) {
            throw new CustomException("You have reached your monthly limit for free plan. Please upgrade to Premium for unlimited report generation.");
        }
    }

    private boolean isUserPremium(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId).stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .collect(Collectors.toList());
        if (subscriptions.isEmpty()) {
            return false;
        }

        Subscription sub = subscriptions.get(0);
        return sub.getSubscriptionPlan().getPrice() > 0;
    }




    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private String saveReportFile(byte[] pdfContent, String reportTitle) {
        try {
            String filePath = "reports/" + reportTitle.replaceAll("\\s", "_") + ".pdf";
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfContent);
            }
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save report file", e);
        }
    }

    private void sendReportEmail(String email, String reportTitle, byte[] pdfContent) {
        String subject = "Your " + reportTitle;
        String message = "Dear User,\n\nPlease find attached your " + reportTitle + ".\n\nBest regards,\nMoneyMinder Team.";
        try {
            emailService.sendEmailWithAttachment(email, subject, message, pdfContent, reportTitle + ".pdf");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send report email", e);
        }
    }

    @Override
    public List<FinancialReportVM> getAllReportsForUser() {
        User user = getCurrentUser();
        return reportRepository.findAllByUserId(user.getId()).stream()
                .map(reportMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public FinancialReportVM getReportById(Long id) {
        FinancialReport report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + id));
        return reportMapper.toVM(report);
    }

    @Override
    public byte[] downloadReport(Long id) {
        FinancialReport report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + id));
        return PdfGenerator.readFileAsBytes(report.getFilePath());
    }

    @Override
    @Scheduled(cron = "0 0 0 1 * ?")
    public void scheduleMonthlyReportGeneration() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            FinancialReportRequest request = FinancialReportRequest.builder()
                    .startDate(getFirstDayOfLastMonth())
                    .endDate(getLastDayOfLastMonth())
                    .reportType(ReportType.MONTHLY)
                    .build();
            generateFinancialReport(request);
        }
    }

    @Override
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void scheduleAnnualReportGeneration() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            FinancialReportRequest request = FinancialReportRequest.builder()
                    .startDate(getFirstDayOfLastYear())
                    .endDate(getLastDayOfLastYear())
                    .reportType(ReportType.ANNUAL)
                    .build();
            generateFinancialReport(request);
        }
    }

    private Date getFirstDayOfLastMonth() {
        return Date.from(java.time.LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }

    private Date getLastDayOfLastMonth() {
        return Date.from(java.time.LocalDate.now().minusMonths(1).withDayOfMonth(java.time.YearMonth.now().atEndOfMonth().getDayOfMonth())
                .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }

    private Date getFirstDayOfLastYear() {
        return Date.from(java.time.LocalDate.now().minusYears(1).withDayOfYear(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }

    private Date getLastDayOfLastYear() {
        return Date.from(java.time.LocalDate.now().minusYears(1).withDayOfYear(java.time.Year.now().isLeap() ? 366 : 365)
                .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }
}
