package com.example.moneyminder.DTOs;

import lombok.Data;

@Data
public class AccountRequest {
    private String accountName;
    private Double balance;
}
