package com.evangeliakostop.playground.models;

import com.evangeliakostop.playground.dto.PaymentIntentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ConfirmPaymentRequest extends PaymentRequest {
    private PaymentIntentDto paymentIntentDto;
}
