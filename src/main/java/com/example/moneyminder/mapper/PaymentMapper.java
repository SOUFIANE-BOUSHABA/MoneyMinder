package com.example.moneyminder.mapper;

import com.example.moneyminder.DTOs.PaymentRequest;
import com.example.moneyminder.VMs.PaymentVM;
import com.example.moneyminder.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "invoiceNumber", source = "invoice.invoiceNumber")
    @Mapping(target = "quoteNumber", source = "quote.quoteNumber")
    PaymentVM toVM(Payment payment);

    @Mapping(target = "id", ignore = true)
    Payment toEntity(PaymentRequest request);
}
