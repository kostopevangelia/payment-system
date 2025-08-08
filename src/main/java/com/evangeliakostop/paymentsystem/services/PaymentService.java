package com.evangeliakostop.paymentsystem.services;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentStatus;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.PaymentInfo;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
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

    /**
     * Initiate Payment Service.
     *
     * @param request       PaymentRequest
     * @param transactionId String
     * @return PaymentInfo
     */
    public PaymentInfo initiatePayment(PaymentRequest request, String transactionId) {

        try {

            /* PaymentIntent */
            PaymentIntentDto paymentIntent = stripe.initPayment(request.getAmount(), request.getCurrency(), request.getPaymentType(), transactionId);

            /* If status is requires_payment_method, then call /confirm */
            if (paymentIntent.getStatus().equals(PaymentStatus.REQUIRES_PAYMENT_METHOD.getDescription())) {
                paymentIntent = stripe.confirmIntent(paymentIntent);
            }
            paymentsDBAccess.insertInitTransaction(transactionId, request.getTransactionType(), request.getAmount(), request.getCurrency());

            return createClientResponse(paymentIntent, transactionId);

        } catch (Exception e) {
            log.error("Method initiatePayment - Exception: {}", e.getMessage());

            throw new CustomException(
                    "PaymentService - error",
                    e.getMessage(),
                    transactionId,
                    ErrorLevelEnum.APPLICATION_ERROR
            );

        }
    }

    private PaymentInfo createClientResponse(PaymentIntentDto paymentIntentDto, String transactionId) {
        return PaymentInfo.builder()
                .client_secret(paymentIntentDto.getClientSecret())
                .transactionId(transactionId)
                .amount(String.valueOf(paymentIntentDto.getAmount()))
                .currency(paymentIntentDto.getCurrency())
                .paymentType("card")
                .status(paymentIntentDto.getStatus())
                .message(paymentIntentDto.getDescription())
                .build();
    }
}
