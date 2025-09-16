package com.evangeliakostop.paymentsystem.controllers;

import com.evangeliakostop.paymentsystem.TestHelper;
import com.evangeliakostop.paymentsystem.common.utils.CommonService;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.FraudPrediction;
import com.evangeliakostop.paymentsystem.models.PaymentInfo;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.services.FraudService;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private RestTemplate restTemplateFraudApi;
    @Mock
    private RestTemplate restTemplateStripe;
    @Mock
    private CommonService commonService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private FraudService fraudService;
    @Mock
    private HttpSession httpSession;
    @Mock
    private StripeIntegration stripe;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentController controller;

    @Test
    void completePayment_Success() throws Exception {
        String jsonRequest = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonRequest);

        String jsonResponse = "src/test/resources/PaymentResponse.json";
        PaymentResponse mockedResponse = TestHelper.createPaymentResponseFromJson(jsonResponse);

        String jsonPaymentIntent = "src/test/resources/PaymentIntentDTO.json";
        PaymentIntentDto paymentIntentDto = TestHelper.createPaymentIntentDTOFromJson(jsonPaymentIntent);

        String jsonPaymentIntentConfirmed = "src/test/resources/StripeResponse_Confirm.json";
        PaymentIntentDto paymentIntentConfirmed = TestHelper.createPaymentIntentDTOFromJson(jsonPaymentIntentConfirmed);

        String jsonPaymentInfo = "src/test/resources/PaymentInfo.json";
        PaymentInfo paymentInfo = TestHelper.createPaymentInfoFromJson(jsonPaymentInfo);

        when(stripe.initPayment(any(), anyString())).thenReturn(paymentIntentDto);
        when(fraudService.getFraudScore(any(), anyString())).thenReturn(FraudPrediction.builder().isFraud(false).fraudScore(1.2).build());
        when(stripe.confirmIntent(any())).thenReturn(paymentIntentConfirmed);

        when(paymentService.initiatePayment(any(), anyString())).thenReturn(paymentInfo);

        ResponseEntity<PaymentResponse> response = controller.initPayment(request);

        assertNotNull(response.getBody());
        assertEquals(mockedResponse.getPaymentInfo().getAmount(), response.getBody().getPaymentInfo().getAmount());

    }


}