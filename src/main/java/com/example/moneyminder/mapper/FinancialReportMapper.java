package com.example.moneyminder.mapper;

import com.example.moneyminder.DTOs.FinancialReportRequest;
import com.example.moneyminder.VMs.FinancialReportVM;
import com.example.moneyminder.entity.FinancialReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FinancialReportMapper {

    @Mapping(source = "user.id", target = "userId")
    FinancialReportVM toVM(FinancialReport financialReport);

    FinancialReport toEntity(FinancialReportRequest request);
}
