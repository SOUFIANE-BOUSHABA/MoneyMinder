package com.example.moneyminder.repository;

import com.example.moneyminder.entity.Invoice;
import com.example.moneyminder.entity.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByInvoiceNumber(String invoiceNumber);
    List<Invoice> findAllByStatus(InvoiceStatus status);
    List<Invoice> findAllByUser_IdAndIssueDateBetween(Long userId, Date startDate, Date endDate);
}
