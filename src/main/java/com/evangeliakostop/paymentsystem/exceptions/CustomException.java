package com.evangeliakostop.paymentsystem.exceptions;

import com.evangeliakostop.paymentsystem.utils.enumeration.ErrorLevelEnum;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
public class CustomException extends RuntimeException {

    private final String message;
    private final String exceptionMessage;
    private final String transactionId;
    private final ErrorLevelEnum errorLevelEnum;


    public CustomException(String message, String exceptionMessage, String transactionId, ErrorLevelEnum errorLevelEnum) {
        super();
        this.message = message;
        this.exceptionMessage = exceptionMessage;
        this.transactionId = transactionId;
        this.errorLevelEnum = errorLevelEnum;
    }
}
