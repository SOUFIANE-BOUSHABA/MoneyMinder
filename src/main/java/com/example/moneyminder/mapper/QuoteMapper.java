package com.example.moneyminder.mapper;

import com.example.moneyminder.DTOs.QuoteRequest;
import com.example.moneyminder.VMs.QuoteVM;
import com.example.moneyminder.entity.Quote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    @Mapping(source = "user.id", target = "userId")
    QuoteVM toVM(Quote quote);

    Quote toEntity(QuoteRequest quoteRequest);
}