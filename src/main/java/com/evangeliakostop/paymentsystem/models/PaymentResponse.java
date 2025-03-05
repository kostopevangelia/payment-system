package com.evangeliakostop.paymentsystem.models;

import com.evangeliakostop.paymentsystem.utils.enumeration.PaymentStatus;
import com.evangeliakostop.paymentsystem.utils.enumeration.PaymentType;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PaymentResponse {
    private String transactionId;
    private String amount;
    private String currency;
    private LocalDateTime timestamp;
    private PaymentType paymentType;
    private PaymentStatus status;
    private double fraudScore;
    private boolean isFraud;
    private String message;
}
