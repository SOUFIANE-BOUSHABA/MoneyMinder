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
        return invoiceMapper.toVM(invoiceRepository.save(invoice));
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
}
