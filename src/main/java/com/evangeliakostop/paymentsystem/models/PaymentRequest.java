package com.evangeliakostop.paymentsystem.models;

import com.evangeliakostop.paymentsystem.utils.enumeration.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
public class PaymentRequest {
    private String cardNumber;
    private String iban;
    private String transactionId;
    private Long amount;
    private String currency;
    private LocalDateTime timestamp;
    private String senderAccount;
    private String receiverAccount;
    private PaymentType paymentType;
    private String userId;
}
