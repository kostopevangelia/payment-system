package com.evangeliakostop.paymentsystem.models;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentStatus;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PaymentResponse {
    private CommonResponse commonResponse;
    private String transactionId;
    private String amount;
    private String currency;
    private LocalDateTime timestamp;
    private PaymentType paymentType;
    private PaymentStatus status;
    private double fraudScore;
    private boolean isFraud;
    private String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentResponse that = (PaymentResponse) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(currency, that.currency); // Exclude transactionId
    }
}
