package com.evangeliakostop.paymentsystem.utils.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {

    PENDING(0, "Payment is still processed."),
    CAPTURED(1, "Payment was successfully completed."),
    FAILED(2, "Payment failed due to an error."),
    REFUNDED(3, "Payment was refunded.");

    private Integer code;
    private String description;
}
