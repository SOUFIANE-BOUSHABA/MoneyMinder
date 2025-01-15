package com.example.moneyminder.service;

import com.example.moneyminder.DTOs.InvoiceRequest;
import com.example.moneyminder.VMs.InvoiceVM;
import com.example.moneyminder.entity.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
    InvoiceVM createInvoice(InvoiceRequest request);

    InvoiceVM updateInvoice(Long id, InvoiceRequest request);

    InvoiceVM getInvoiceById(Long id);

    List<InvoiceVM> getAllInvoices();

    void deleteInvoice(Long id);

    InvoiceVM updateInvoiceStatus(Long id, InvoiceStatus status);

}
