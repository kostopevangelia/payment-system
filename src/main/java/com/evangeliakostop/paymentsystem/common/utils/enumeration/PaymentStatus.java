package com.evangeliakostop.paymentsystem.common.utils.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {

    REQUIRES_PAYMENT_METHOD(0, "requires_payment_method"),
    PENDING(1, "Pending"),
    COMPLETED(2, "Completed"),
    FAILED(3, "Failed"),
    CANCELLED(4, "Cancelled");

    private Integer code;
    private String description;
}
