package com.evangeliakostop.paymentsystem.common.utils.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {

    INITIALIZED(0, "Initialized"),
    PENDING(1, "Pending"),
    COMPLETED(2, "Completed"),
    FAILED(3, "Failed"),
    CANCELLED(4, "Cancelled");

    private Integer code;
    private String description;
}
