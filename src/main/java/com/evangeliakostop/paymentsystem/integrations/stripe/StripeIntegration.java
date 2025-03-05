package com.evangeliakostop.paymentsystem.integrations.stripe;

import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.utils.enumeration.ErrorLevelEnum;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class StripeIntegration {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final String secretKey = "sk_test_51Qxp74BTS3g9NVDlCdHfk9K1beKmz07Bib0gKIJ6bKHiIk5pbmH9riUaiAiXe1zQ8RvIuDZ2uzzsdWCgCZlprmHK00Yjj4BHFm";

    private final RestTemplate restTemplateStripe;

    public StripeIntegration(RestTemplate restTemplateStripe) {
        this.restTemplateStripe = restTemplateStripe;
    }

    public PaymentIntentDto initPayment(Long amount, String currency, String paymentType, String transactionId) throws StripeException {

        String url = "https://api.stripe.com/v1/payment_intents";

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("amount", String.valueOf(amount));
        requestParams.add("currency", currency);
        requestParams.add("payment_method_types[]", paymentType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + secretKey);  // Use your Stripe secret key here

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestParams, headers);

        try {
            ResponseEntity<PaymentIntentDto> response = restTemplateStripe.exchange(url, HttpMethod.POST, entity, PaymentIntentDto.class);
            if (response.getBody() != null) {
                return response.getBody();
            } else {
                throw new Exception("Error response from stripe: " + response.getStatusCode());
            }
        } catch (final Exception e) {
            throw new CustomException( e.getMessage(),
                    "Error while initiating payment",
                    null,
                    ErrorLevelEnum.APPLICATION_ERROR);
        }
    }

    public void submitPayment() {
    }

    public void getInfo() {
    }
}
