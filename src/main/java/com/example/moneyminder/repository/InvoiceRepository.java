package com.example.moneyminder.repository;

import com.example.moneyminder.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByInvoiceNumber(String invoiceNumber);
}
