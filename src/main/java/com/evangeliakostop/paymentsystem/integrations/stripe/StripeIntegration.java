package com.evangeliakostop.paymentsystem.integrations.stripe;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class StripeIntegration {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private static final String SECRET_KEY = "sk_test_51Qxp74BTS3g9NVDlCdHfk9K1beKmz07Bib0gKIJ6bKHiIk5pbmH9riUaiAiXe1zQ8RvIuDZ2uzzsdWCgCZlprmHK00Yjj4BHFm";

    private final RestTemplate restTemplateStripe;

    public StripeIntegration(@Qualifier("restTemplateStripe") RestTemplate restTemplateStripe) {
        this.restTemplateStripe = restTemplateStripe;
    }

    /**
     * Init Payment
     *
     * @param amount        Long
     * @param currency      String
     * @param paymentType   String
     * @param transactionId String
     * @return the PaymentIntentDto
     */
    public PaymentIntentDto initPayment(Long amount, String currency, String paymentType, String transactionId) {

        String url = "https://api.stripe.com/v1/payment_intents";

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("amount", String.valueOf(amount));
        requestParams.add("currency", currency);
        requestParams.add("automatic_payment_methods[enabled]", "true");
        requestParams.add("automatic_payment_methods[allow_redirects]", "never");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + SECRET_KEY);  // Use your Stripe secret key here

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestParams, headers);
        ResponseEntity<PaymentIntentDto> response = null;

        try {
            response = restTemplateStripe.exchange(url, HttpMethod.POST, entity, PaymentIntentDto.class);
            if (response.getBody() != null) {
                return response.getBody();
            } else {
                throw new CustomException(
                        "Error response from stripe:",
                        "Response Body cannot be null",
                        null,
                        ErrorLevelEnum.APPLICATION_ERROR
                );
            }
        } catch (Exception e) {
            // Handle generic exceptions
            log.error("initPayment: Stripe error: {}", e.getMessage());
            throw new CustomException(
                    "StripeIntegration - error",
                    e.getMessage(),
                    transactionId,
                    ErrorLevelEnum.APPLICATION_ERROR
            );
        }
    }

    /**
     * Confirm Intent.
     *
     * @param paymentIntent PaymentIntentDto
     * @return PaymentIntentDto
     */
    public PaymentIntentDto confirmIntent(PaymentIntentDto paymentIntent) {
        String url = "https://api.stripe.com/v1/payment_intents/{id}/confirm";  // URL with path parameter

        // Prepare the request parameters
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("payment_method", "pm_card_visa");

        // Set the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + SECRET_KEY);  // Use your Stripe secret key here

        // Create the request entity
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestParams, headers);

        // Prepare the URL template variables (in this case, the PaymentIntent ID)
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", paymentIntent.getId());  // Set the PaymentIntent ID

        try {
            // Perform the HTTP request and exchange the response
            ResponseEntity<PaymentIntentDto> response = restTemplateStripe.exchange(url, HttpMethod.POST, entity, PaymentIntentDto.class, uriVariables);

            if (response.getBody() != null) {
                return response.getBody();
            } else {
                throw new CustomException(
                        "Error response from stripe:",
                        "Response Body cannot be null",
                        null,
                        ErrorLevelEnum.APPLICATION_ERROR
                );
            }
        } catch (Exception e) {
            // Handle generic exceptions
            log.error("confirmIntent: Stripe error: {}", e.getMessage());
            throw new CustomException(
                    "StripeIntegration - error",
                    e.getMessage(),
                    null,
                    ErrorLevelEnum.APPLICATION_ERROR
            );
        }
    }

}
