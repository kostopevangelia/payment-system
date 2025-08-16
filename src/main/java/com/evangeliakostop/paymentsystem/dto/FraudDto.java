package com.evangeliakostop.paymentsystem.dto;

import lombok.Data;

@Data
public class FraudDto {
    private double fraudScore;
    private boolean isFraud;
}