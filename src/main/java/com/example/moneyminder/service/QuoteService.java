package com.example.moneyminder.service;

import com.example.moneyminder.DTOs.QuoteRequest;
import com.example.moneyminder.VMs.QuoteVM;

import java.util.List;

public interface QuoteService {
    QuoteVM createQuote(QuoteRequest request);

    QuoteVM updateQuote(Long id, QuoteRequest request);

    QuoteVM getQuoteById(Long id);

    List<QuoteVM> getAllQuotes();

    void deleteQuote(Long id);

    byte[] generateAndSendQuotePdf(Long id);
}
