package com.evangeliakostop.paymentsystem.exceptions;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
public class CustomException extends RuntimeException {

    private final String message;
    private final String exceptionMessage;
    private final int errorCode;
    private final String transactionId;
    private final ErrorLevelEnum errorLevelEnum;


    public CustomException(String message, String exceptionMessage, int errorCode, String transactionId, ErrorLevelEnum errorLevelEnum) {
        super();
        this.message = message;
        this.exceptionMessage = exceptionMessage;
        this.errorCode = errorCode;
        this.transactionId = transactionId;
        this.errorLevelEnum = errorLevelEnum;
    }
}
