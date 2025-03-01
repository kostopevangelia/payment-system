package com.evangeliakostop.paymentsystem.exceptions;

import com.evangeliakostop.paymentsystem.utils.enumeration.ErrorLevelEnum;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

    private final String message;
    private final String traceId; // TODO
    private final String transactionId;
    private final ErrorLevelEnum errorLevelEnum;


}
