package com.evangeliakostop.paymentsystem.services;

import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
import com.evangeliakostop.paymentsystem.utils.UniqueIdGenerator;
import com.evangeliakostop.paymentsystem.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.utils.enumeration.PaymentStatus;
import com.evangeliakostop.paymentsystem.utils.enumeration.PaymentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentService {

    private final StripeIntegration stripe;
    private final PaymentsDBAccess paymentsDBAccess;

    @Autowired
    public PaymentService(StripeIntegration stripe, PaymentsDBAccess paymentsDBAccess) {
        this.stripe = stripe;
        this.paymentsDBAccess = paymentsDBAccess;
    }

    public PaymentResponse initiatePayment(PaymentRequest request) {

        String transactionId = UniqueIdGenerator.generateSecureToken();

        PaymentIntentDto paymentIntent = null;
        PaymentResponse response = null;
        try {
            paymentIntent = stripe.initPayment(request.getAmount(), request.getCurrency(), request.getPaymentType().getDescription(), transactionId);
            response = createClientResponse(paymentIntent, transactionId);
            paymentsDBAccess.insertInitTransaction(transactionId, request.getTransactionType(), request.getAmount(), request.getCurrency());
        } catch (Exception e) {
            log.error("Method initiatePayment - Exception", e);
            throw new CustomException(e.getMessage(), "Error while initiating the payment: {}", null, ErrorLevelEnum.APPLICATION_ERROR);
            //throw new RuntimeException(e.getMessage());
        }

        return response;
    }

    private PaymentResponse createClientResponse(PaymentIntentDto paymentIntentDto, String transactionId) {
        return PaymentResponse.builder()
                .transactionId(transactionId)
                .amount(String.valueOf(paymentIntentDto.getAmount()))
                .currency(paymentIntentDto.getCurrency())
                .paymentType(PaymentType.CARD)
                .status(PaymentStatus.INITIALIZED)
                .message(paymentIntentDto.getDescription())
                .build();
    }

    public PaymentResponse submitPayment(PaymentRequest request, String transactionId) {
        stripe.submitPayment();
        return null;
    }

    public PaymentResponse getPaymentInfo(PaymentRequest request, String transactionId) {
        stripe.getInfo();
        return null;
    }

}
