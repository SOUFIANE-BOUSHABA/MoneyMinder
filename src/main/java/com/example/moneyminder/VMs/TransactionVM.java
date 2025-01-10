package com.example.moneyminder.VMs;

import lombok.Data;
import java.util.Date;

@Data
public class TransactionVM {

    private Long id;
    private Date date;
    private Double amount;
    private String type;
    private Long userId;
    private String userEmail;
    private String categoryName;
    private String description;

}
