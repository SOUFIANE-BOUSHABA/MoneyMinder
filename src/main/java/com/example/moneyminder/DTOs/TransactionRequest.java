package com.example.moneyminder.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Date;

@Data
public class TransactionRequest {
    @NotNull
    private Date date;

    @NotNull
    private Double amount;

    @NotNull
    private String type;

    @NotNull
    private Long categoryId;



    private String description;
}
