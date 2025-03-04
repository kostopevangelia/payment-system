package com.evangeliakostop.paymentsystem.services;

import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.utils.UniqueIdGenerator;
import com.evangeliakostop.paymentsystem.utils.enumeration.ErrorLevelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final StripeIntegration stripe;

    @Autowired
    public PaymentService(StripeIntegration stripe) {
        this.stripe = stripe;
    }

    public PaymentResponse initiatePayment(PaymentRequest request) {

        String transactionId = UniqueIdGenerator.generateSecureToken();

        PaymentIntentDto paymentIntent = null;
        try {
            paymentIntent = stripe.initPayment(request.getAmount(), request.getCurrency(), request.getPaymentType(), transactionId);
        } catch (Exception e) {
            throw new CustomException("Error while initiating the payment", null, null, ErrorLevelEnum.APPLICATION_ERROR);
        }

        return createClientResponse(paymentIntent);
    }

    private PaymentResponse createClientResponse(PaymentIntentDto paymentIntentDto) {
        return PaymentResponse.builder()
                .amount(String.valueOf(paymentIntentDto.getAmount()))
                .currency(paymentIntentDto.getCurrency())
                .paymentType("Card")
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
