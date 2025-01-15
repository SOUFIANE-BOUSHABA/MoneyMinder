package com.example.moneyminder.service;

import com.example.moneyminder.DTOs.InvoiceRequest;
import com.example.moneyminder.VMs.InvoiceVM;

import java.util.List;

public interface InvoiceService {
    InvoiceVM createInvoice(InvoiceRequest request);

    InvoiceVM updateInvoice(Long id, InvoiceRequest request);

    InvoiceVM getInvoiceById(Long id);

    List<InvoiceVM> getAllInvoices();

    void deleteInvoice(Long id);
}
