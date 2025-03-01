package com.evangeliakostop.paymentsystem.models;

import com.evangeliakostop.paymentsystem.utils.enumeration.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class PaymentResponse {
    private String transactionId;
    private double amount;
    private String currency;
    private LocalDateTime timestamp;
    private String senderAccount;
    private String receiverAccount;
    private PaymentType paymentType;
    private double fraudScore;
    private boolean isFraud;
    private String message;
}
