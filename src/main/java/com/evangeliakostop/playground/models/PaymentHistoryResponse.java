package com.evangeliakostop.playground.models;

import com.evangeliakostop.playground.utils.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class PaymentHistoryResponse {
    private String transactionId;
    private Double amount;
    private PaymentStatus status;
    private String date;
}
