package com.evangeliakostop.paymentsystem.integrations;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.models.ConfirmPaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentInfo;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@Slf4j
public class PaymentMsIntegration {
    private final RestTemplate restTemplatePaymentsMs;

    public PaymentMsIntegration(@Qualifier("restTemplatePaymentMs") RestTemplate restTemplatePaymentsMs) {
        this.restTemplatePaymentsMs = restTemplatePaymentsMs;
    }

    public PaymentInfo confirmPayment(PaymentRequest request, PaymentIntentDto paymentIntent, String sessionId) {
        String url = "http://localhost:8080/payments/confirm";

        ConfirmPaymentRequest prepareConfirmRequest = new ConfirmPaymentRequest();
        BeanUtils.copyProperties(request, prepareConfirmRequest);
        prepareConfirmRequest.setPaymentIntentDto(paymentIntent);

        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", "JSESSION=" + sessionId);

        // Create the HttpEntity with the request body and headers
        HttpEntity<ConfirmPaymentRequest> entity = new HttpEntity<>(prepareConfirmRequest, headers);

        try {
            ResponseEntity<PaymentResponse> response = restTemplatePaymentsMs.exchange(url, HttpMethod.POST, entity, PaymentResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getPaymentInfo();
            } else {
                throw new RuntimeException("Error calling other endpoint: " + response.getStatusCode());
            }
        } catch (Exception e) {
            // Handle generic exceptions
            log.error("initPayment: Stripe error: {}", e.getMessage());
            throw new CustomException(
                    "StripeIntegration - error",
                    e.getMessage(),
                    null,
                    ErrorLevelEnum.APPLICATION_ERROR
            );
        }
    }
}
