package com.evangeliakostop.paymentsystem.integrations;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.dto.FraudApiRequest;
import com.evangeliakostop.paymentsystem.dto.FraudDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class FraudApiIntegration {
    private static final String SECRET_KEY = "";
    private final RestTemplate restTemplateFraudApi;

    public FraudApiIntegration(RestTemplate restTemplateFraudApi) {
        this.restTemplateFraudApi = restTemplateFraudApi;
    }

    public FraudDto predictFraud(FraudApiRequest request, String transactionId) {
        String url = "url";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + SECRET_KEY);

        HttpEntity<FraudApiRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<FraudDto> response = null;

        try {
            response = restTemplateFraudApi.exchange(url, HttpMethod.POST, entity, FraudDto.class);
            if (response.getBody() != null) {
                return response.getBody();
            } else {
                throw new CustomException(
                        "Error response from fraud api:",
                        "Response Body cannot be null",
                        null,
                        ErrorLevelEnum.APPLICATION_ERROR
                );
            }
        } catch (Exception e) {
            // Handle generic exceptions
            log.error("predictFraud: FraudApi error: {}", e.getMessage());
            throw new CustomException(
                    "FraudApiIntegration - error",
                    e.getMessage(),
                    transactionId,
                    ErrorLevelEnum.APPLICATION_ERROR
            );
        }
    }
}
