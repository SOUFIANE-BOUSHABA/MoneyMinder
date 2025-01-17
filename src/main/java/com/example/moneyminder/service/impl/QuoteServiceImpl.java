package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.QuoteRequest;
import com.example.moneyminder.VMs.QuoteVM;
import com.example.moneyminder.entity.Invoice;
import com.example.moneyminder.entity.Quote;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.entity.enums.QuoteStatus;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.QuoteMapper;
import com.example.moneyminder.repository.QuoteRepository;
import com.example.moneyminder.repository.UserRepository;
import com.example.moneyminder.service.QuoteService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.moneyminder.utils.PdfGenerator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;
    private final UserRepository userRepository;
    private final EmailService emailService;


    public User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return currentUser;
    }


    @Override
    public QuoteVM createQuote(QuoteRequest request) {
        User currentUser = getCurrentUser();
        Quote quote = quoteMapper.toEntity(request);
        quote.setUser(currentUser);
        quote.setStatus(QuoteStatus.DRAFT);

        Quote savedQuote = quoteRepository.save(quote);


        String subject = "Quote #" + savedQuote.getQuoteNumber();
        String text = generateQuoteEmailContent(savedQuote);
        try {
            emailService.sendEmail(currentUser.getEmail(), subject, text);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


        return quoteMapper.toVM(savedQuote);

    }

    @Override
    public QuoteVM updateQuote(Long id, QuoteRequest request) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with ID: " + id));
        quote.setQuoteNumber(request.getQuoteNumber());
        quote.setIssueDate(request.getIssueDate());
        quote.setTotalAmount(request.getTotalAmount());
        quote.setStatus(request.getStatus());
        return quoteMapper.toVM(quoteRepository.save(quote));
    }

    @Override
    public QuoteVM getQuoteById(Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with ID: " + id));
        return quoteMapper.toVM(quote);
    }

    @Override
    public List<QuoteVM> getAllQuotes() {
        return quoteRepository.findAll().stream()
                .map(quoteMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteQuote(Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with ID: " + id));
        quoteRepository.delete(quote);
    }












      private String generateQuoteEmailContent(Quote quote) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>")
                .append("<body style='font-family: Arial, sans-serif;'>")
                .append("<p>Dear <b>").append(quote.getUser().getFirstName()).append(" ").append(quote.getUser().getLastName()).append("</b>,</p>")
                .append("<p>Here are the details of your quote:</p>")
                .append("<p>--------------------------------------------------</p>")
                .append("<p><b>Quote Number:</b> <span style='color: red;'>").append(quote.getQuoteNumber()).append("</span></p>")
                .append("<p><b>Issue Date:</b> <span style='color: red;'>").append(quote.getIssueDate()).append("</span></p>")
                .append("<p><b>Total Amount:</b> <span style='color: red;'>$").append(quote.getTotalAmount()).append("</span></p>")
                .append("<p><b>Status:</b> <span style='color: red;'>").append(quote.getStatus()).append("</span></p>")
                .append("<p>--------------------------------------------------</p>")
                .append("<p>Thank you for using <b><span style='color: blue;'>MoneyMinder</span></b>. If you have any questions, please contact us.</p>")
                .append("<p>Best regards,</p>")
                .append("<p><b><span style='color: blue;'>MoneyMinder Team</span></b></p>")
                .append("</body>")
                .append("</html>");
        return sb.toString();
     }



    @Override
    public byte[] generateAndSendQuotePdf(Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with ID: " + id));

        byte[] pdfContent = PdfGenerator.generateQuotePdf(quote);

        String subject = "Your Quote #" + quote.getQuoteNumber();
        String text = "Dear " + quote.getUser().getFirstName() + ",\n\nPlease find your quote attached.\n\nBest regards,\nMoneyMinder Team";

        try {
            emailService.sendEmailWithAttachment(quote.getUser().getEmail(), subject, text, pdfContent, "quote_" + id + ".pdf");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }

        return pdfContent;
    }





}