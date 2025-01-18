package com.example.moneyminder.service.impl;

import com.example.moneyminder.entity.Invoice;
import com.example.moneyminder.entity.enums.InvoiceStatus;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.repository.InvoiceRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceSchedulerService {

    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;

    @Scheduled(fixedRate = 10800000)
    public void checkInvoices() {
        List<Invoice> pendingInvoices = invoiceRepository.findAllByStatus(InvoiceStatus.PENDING);

        for (Invoice invoice : pendingInvoices) {
            long daysUntilDue = calculateDaysUntilDue(invoice.getIssueDate());

            if (daysUntilDue == 7) {
                sendReminderEmail(invoice, "Reminder: 7 days left to pay your invoice");
            } else if (daysUntilDue == 2) {
                sendReminderEmail(invoice, "Warning: 2 days left to pay your invoice");
            } else if (daysUntilDue < 0) {
                updateInvoiceToOverdue(invoice);
            }
        }
    }

    private long calculateDaysUntilDue(Date issueDate) {
        LocalDate issueLocalDate;

        if (issueDate instanceof java.sql.Date) {
            issueLocalDate = ((java.sql.Date) issueDate).toLocalDate();
        } else {
            issueLocalDate = issueDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        LocalDate dueDate = issueLocalDate.plusDays(30);
        LocalDate today = LocalDate.now();
        return dueDate.toEpochDay() - today.toEpochDay();
    }


    private void sendReminderEmail(Invoice invoice, String subject) {
        String emailContent = generateInvoiceReminderEmailContent(invoice);
        try {
            emailService.sendEmail(invoice.getUser().getEmail(), subject, emailContent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void updateInvoiceToOverdue(Invoice invoice) {
        invoice.setStatus(InvoiceStatus.OVERDUE);
        invoiceRepository.save(invoice);

        String subject = "Invoice Overdue: " + invoice.getInvoiceNumber();
        String emailContent = generateInvoiceOverdueEmailContent(invoice);
        try {
            emailService.sendEmail(invoice.getUser().getEmail(), subject, emailContent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateInvoiceReminderEmailContent(Invoice invoice) {
        double totalPaid = invoice.getPayments().stream()
                .mapToDouble(payment -> payment.getAmount())
                .sum();
        double remainingAmount = invoice.getTotalAmount() - totalPaid;
        double percentagePaid = (totalPaid / invoice.getTotalAmount()) * 100;

        return String.format(
                "<html><body>" +
                        "<h3>Invoice Reminder</h3>" +
                        "<p>Dear %s,</p>" +
                        "<p>This is a reminder that your invoice <b>#%s</b> is due in %d days.</p>" +
                        "<p>Invoice Total: $%.2f</p>" +
                        "<p>Amount Paid: $%.2f (%.2f%%)</p>" +
                        "<p>Remaining Balance: $%.2f</p>" +
                        "<br><p>Thank you for your prompt payment!</p>" +
                        "</body></html>",
                invoice.getUser().getFirstName(),
                invoice.getInvoiceNumber(),
                calculateDaysUntilDue(invoice.getIssueDate()),
                invoice.getTotalAmount(),
                totalPaid,
                percentagePaid,
                remainingAmount
        );
    }

    private String generateInvoiceOverdueEmailContent(Invoice invoice) {
        return String.format(
                "<html><body>" +
                        "<h3>Invoice Overdue</h3>" +
                        "<p>Dear %s,</p>" +
                        "<p>Your invoice <b>#%s</b> is now overdue.</p>" +
                        "<p>Please make the payment as soon as possible to avoid further action.</p>" +
                        "<br><p>Thank you!</p>" +
                        "</body></html>",
                invoice.getUser().getFirstName(),
                invoice.getInvoiceNumber()
        );
    }
}
