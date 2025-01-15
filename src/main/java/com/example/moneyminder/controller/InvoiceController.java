package com.example.moneyminder.controller;

import com.example.moneyminder.DTOs.InvoiceRequest;
import com.example.moneyminder.VMs.InvoiceVM;
import com.example.moneyminder.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceVM> createInvoice(@RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceVM> updateInvoice(@PathVariable Long id, @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceVM> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceVM>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
