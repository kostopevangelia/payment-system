package com.evangeliakostop.paymentsystem.services;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentStatus;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.integrations.PaymentMsIntegration;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.ConfirmPaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentInfo;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentService {

    private final PaymentMsIntegration paymentMsIntegration;
    private final StripeIntegration stripe;
    private final PaymentsDBAccess paymentsDBAccess;

    @Autowired
    public PaymentService(PaymentMsIntegration paymentMsIntegration, StripeIntegration stripe, PaymentsDBAccess paymentsDBAccess) {
        this.paymentMsIntegration = paymentMsIntegration;
        this.stripe = stripe;
        this.paymentsDBAccess = paymentsDBAccess;
    }

    public PaymentInfo initiatePayment(PaymentRequest request, String transactionId, String sessionId) {

        try {

            /* PaymentIntent */
            PaymentIntentDto paymentIntent = stripe.initPayment(request.getAmount(), request.getCurrency(), request.getPaymentType(), transactionId);

            /* If status is requires_payment_method, then call /confirm */
            if (paymentIntent.getStatus().equals(PaymentStatus.REQUIRES_PAYMENT_METHOD.getDescription())) {
                return paymentMsIntegration.confirmPayment(request, paymentIntent, sessionId);
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

    public PaymentInfo confirmPayment(ConfirmPaymentRequest request, String transactionId) {

        try {

            /* confirm Intent */
            PaymentIntentDto confirmPaymentIntent = stripe.confirmIntent(request.getPaymentIntentDto());

            return createClientResponse(confirmPaymentIntent, transactionId);

        } catch (Exception e) {
            log.error("Method confirmPayment - Exception: {}", e.getMessage());

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
