package com.evangeliakostop.paymentsystem.utils.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentType {

    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    IBAN("IBAN");

    private final String description;
}
