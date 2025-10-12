package com.evangeliakostop.playground.models;

import com.evangeliakostop.playground.utils.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class PaymentStatusResponse {
    private String transactionId;
    private PaymentStatus status;
}
