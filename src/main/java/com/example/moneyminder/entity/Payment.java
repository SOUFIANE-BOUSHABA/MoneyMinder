package com.example.moneyminder.entity;

import com.example.moneyminder.entity.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Temporal(TemporalType.DATE)
    private Date paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "quote_id")
    private Quote quote;
}
