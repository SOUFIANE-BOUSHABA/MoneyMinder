package com.example.moneyminder.VMs;

import com.example.moneyminder.entity.enums.SubscriptionStatus;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionVM {
    private Long id;
    private String planName;
    private SubscriptionStatus status;
    private Date startDate;
    private Date endDate;
}
