package com.example.moneyminder.mapper;

import com.example.moneyminder.DTOs.InvoiceRequest;
import com.example.moneyminder.VMs.InvoiceVM;
import com.example.moneyminder.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "user.id", target = "userId")
    InvoiceVM toVM(Invoice invoice);

    Invoice toEntity(InvoiceRequest invoiceRequest);
}