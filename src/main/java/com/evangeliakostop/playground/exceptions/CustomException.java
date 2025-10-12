package com.evangeliakostop.playground.exceptions;

import com.evangeliakostop.playground.utils.enumeration.ErrorLevelEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
public class CustomException extends RuntimeException {
    private final String message;
    private final String exceptionMessage;
    private final String transactionId;
    private final ErrorLevelEnum errorLevelEnum;


    public CustomException(String exceptionMessage, String message, String transactionId, ErrorLevelEnum errorLevelEnum) {
        super();
        this.exceptionMessage = exceptionMessage;
        this.message = message;
        this.transactionId = transactionId;
        this.errorLevelEnum = errorLevelEnum;
    }
}
