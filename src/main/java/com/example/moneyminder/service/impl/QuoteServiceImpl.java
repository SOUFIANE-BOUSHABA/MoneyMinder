package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.QuoteRequest;
import com.example.moneyminder.VMs.QuoteVM;
import com.example.moneyminder.entity.Quote;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.entity.enums.QuoteStatus;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.QuoteMapper;
import com.example.moneyminder.repository.QuoteRepository;
import com.example.moneyminder.repository.UserRepository;
import com.example.moneyminder.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;
    private final UserRepository userRepository;


    public  User getCurrentUser() {
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
        return quoteMapper.toVM(quoteRepository.save(quote));
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
}
