package com.example.moneyminder.VMs;

import lombok.Data;

@Data
public class AccountVM {
    private Long id;
    private String accountName;
    private Double balance;
    private Long userId;
}
