package com.evangeliakostop.paymentsystem.exceptions;

import com.evangeliakostop.paymentsystem.utils.enumeration.ErrorLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class CustomException extends RuntimeException {

    private final String message;
    private final String traceId; // TODO
    private final String transactionId;
    private final ErrorLevelEnum errorLevelEnum;


}
