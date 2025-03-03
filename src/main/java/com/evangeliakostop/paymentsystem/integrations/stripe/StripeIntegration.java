package com.evangeliakostop.paymentsystem.integrations.stripe;

import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.utils.enumeration.PaymentType;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class StripeIntegration {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final RestTemplate restTemplateStripe;

    public StripeIntegration(RestTemplate restTemplateStripe) {
        this.restTemplateStripe = restTemplateStripe;
    }

    public PaymentIntentDto initPayment(Long amount, String currency, PaymentType paymentType) throws StripeException {
//        PaymentIntentCreateParams params =
//                PaymentIntentCreateParams.builder()
//                        .setAmount(amount)
//                        .setCurrency(currency)
//                        .setAutomaticPaymentMethods(
//                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
//                                        .setEnabled(true)
//                                        .build()
//                        )
//                        .build();
//
//        return PaymentIntent.create(params);
        String url = "https://api.stripe.com/v1/payment_intents";

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("amount", amount);
        requestParams.put("currency", currency);
        requestParams.put("payment_method_types[]", paymentType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + stripeSecretKey);  // Use your Stripe secret key here

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestParams, headers);

        try {
            ResponseEntity<PaymentIntentDto> response = restTemplateStripe.exchange(url, HttpMethod.POST, entity, PaymentIntentDto.class);
            if (response.getBody() != null) {
                return response.getBody();
            } else {
                throw new Exception("Error response from stripe: " + response.getStatusCode());
            }
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException("Error while initiating payment",
                    null,
                    null,
                    ErrorLevelEnum.APPLICATION_ERROR);
        }
    }

    public void submitPayment() {
    }

    public void getInfo() {
    }
}
