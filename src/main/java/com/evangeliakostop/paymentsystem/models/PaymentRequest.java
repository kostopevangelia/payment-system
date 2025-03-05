package com.evangeliakostop.paymentsystem.models;

import com.evangeliakostop.paymentsystem.utils.enumeration.PaymentType;
import com.evangeliakostop.paymentsystem.utils.enumeration.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PaymentRequest {
    private String cardNumber;
    private String iban;
    private String transactionId;
    private Long amount;
    private String currency;
    private LocalDateTime timestamp;
    private TransactionType transactionType;
    private PaymentType paymentType;
    private String userId;
}
