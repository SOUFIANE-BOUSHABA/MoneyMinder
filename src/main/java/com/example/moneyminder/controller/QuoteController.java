package com.example.moneyminder.controller;

import com.example.moneyminder.DTOs.QuoteRequest;
import com.example.moneyminder.VMs.QuoteVM;
import com.example.moneyminder.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteVM> createQuote(@RequestBody QuoteRequest request) {
        return ResponseEntity.ok(quoteService.createQuote(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuoteVM> updateQuote(@PathVariable Long id, @RequestBody QuoteRequest request) {
        return ResponseEntity.ok(quoteService.updateQuote(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteVM> getQuoteById(@PathVariable Long id) {
        return ResponseEntity.ok(quoteService.getQuoteById(id));
    }

    @GetMapping
    public ResponseEntity<List<QuoteVM>> getAllQuotes() {
        return ResponseEntity.ok(quoteService.getAllQuotes());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        quoteService.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateAndSendQuotePdf(@PathVariable Long id) {
        byte[] pdfContent = quoteService.generateAndSendQuotePdf(id);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=quote_" + id + ".pdf")
                .body(pdfContent);
    }

}
