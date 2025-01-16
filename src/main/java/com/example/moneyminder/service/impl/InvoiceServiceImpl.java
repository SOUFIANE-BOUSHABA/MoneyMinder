package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.InvoiceRequest;
import com.example.moneyminder.VMs.InvoiceVM;
import com.example.moneyminder.entity.Invoice;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.entity.enums.InvoiceStatus;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.InvoiceMapper;
import com.example.moneyminder.repository.InvoiceRepository;
import com.example.moneyminder.repository.UserRepository;
import com.example.moneyminder.service.InvoiceService;
import com.example.moneyminder.utils.PdfGenerator;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final UserRepository userRepository;
    private final EmailService emailService;


    public  User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return currentUser;
    }

    @Override
    public InvoiceVM createInvoice(InvoiceRequest request) {
        User currentUser = getCurrentUser();

        Invoice invoice = invoiceMapper.toEntity(request);
        invoice.setUser(currentUser);
        invoice.setStatus(InvoiceStatus.PENDING);

        Invoice savedInvoice = invoiceRepository.save(invoice);


        String subject = "Invoice #" + savedInvoice.getInvoiceNumber();
        String text = generateInvoiceEmailContent(savedInvoice);
        try {
            emailService.sendEmail(currentUser.getEmail(), subject, text);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


        return invoiceMapper.toVM(savedInvoice);
    }

    @Override
    public InvoiceVM updateInvoice(Long id, InvoiceRequest request) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setIssueDate(request.getIssueDate());
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setStatus(request.getStatus());
        return invoiceMapper.toVM(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceVM getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));
        return invoiceMapper.toVM(invoice);
    }

    @Override
    public List<InvoiceVM> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));
        invoiceRepository.delete(invoice);
    }



    @Override
    public InvoiceVM updateInvoiceStatus(Long id, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));
        invoice.setStatus(status);
        return invoiceMapper.toVM(invoiceRepository.save(invoice));
    }














    private String generateInvoiceEmailContent(Invoice invoice) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>")
                .append("<body style='font-family: Arial, sans-serif;'>")
                .append("<p>Dear <b>").append(invoice.getUser().getFirstName()).append(" ").append(invoice.getUser().getLastName()).append("</b>,</p>")
                .append("<p>Here are the details of your invoice:</p>")
                .append("<p>--------------------------------------------------</p>")
                .append("<p><b>Invoice Number:</b> <span style='color: red;'>").append(invoice.getInvoiceNumber()).append("</span></p>")
                .append("<p><b>Issue Date:</b> <span style='color: red;'>").append(invoice.getIssueDate()).append("</span></p>")
                .append("<p><b>Total Amount:</b> <span style='color: red;'>$").append(invoice.getTotalAmount()).append("</span></p>")
                .append("<p><b>Status:</b> <span style='color: red;'>").append(invoice.getStatus()).append("</span></p>")
                .append("<p>--------------------------------------------------</p>")
                .append("<p>Thank you for using <b><span style='color: blue;'>MoneyMinder</span></b>. If you have any questions, please contact us.</p>")
                .append("<p>Best regards,</p>")
                .append("<p><b><span style='color: blue;'>MoneyMinder Team</span></b></p>")
                .append("</body>")
                .append("</html>");
        return sb.toString();
    }



    @Override
    public byte[] generateAndSendInvoicePdf(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        byte[] pdfContent = PdfGenerator.generateInvoicePdf(invoice);

        String subject = "Your Invoice #" + invoice.getInvoiceNumber();
        String text = "Dear " + invoice.getUser().getFirstName() + ",\n\nPlease find your invoice attached.\n\nBest regards,\nMoneyMinder Team";
        try {
            emailService.sendEmailWithAttachment(invoice.getUser().getEmail(), subject, text, pdfContent, "invoice_" + id + ".pdf");
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }

        return pdfContent;
    }






}
