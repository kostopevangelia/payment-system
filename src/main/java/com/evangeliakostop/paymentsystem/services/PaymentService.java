package com.evangeliakostop.paymentsystem.services;

import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
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

        PaymentIntentDto paymentIntentDto = null;
        try {
            paymentIntentDto = stripe.initPayment(request.getAmount(), request.getCurrency(), request.getPaymentType());
        } catch (Exception e) {
            throw new CustomException("Error while initiating the payment", null, null, ErrorLevelEnum.APPLICATION_ERROR);
        }

        return createClientResponse(paymentIntentDto);
    }

    private PaymentResponse createClientResponse(PaymentIntentDto paymentIntentDto) {
        return PaymentResponse.builder().build();
    }

    public PaymentResponse submitPayment() {
        stripe.submitPayment();
        return null;
    }

    public PaymentResponse getPaymentInfo() {
        stripe.getInfo();
        return null;
    }
}
