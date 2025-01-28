package com.evangeliakostop.paymentsystem.utils.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType {

    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    IBAN("IBAN");

    private String description;
}
