package com.evangeliakostop.playground.utils.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType {
    PAYMENT("PAYMENT"),
    REFUND("REFUND");

    private final String description;

    @Override
    public String toString() {
        return this.description;
    }
}
