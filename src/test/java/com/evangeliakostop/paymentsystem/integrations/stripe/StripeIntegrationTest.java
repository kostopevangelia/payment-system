package com.evangeliakostop.paymentsystem.integrations.stripe;

import com.evangeliakostop.paymentsystem.TestHelper;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StripeIntegrationTest {

    @Mock
    private RestTemplate restTemplateStripe;

    @InjectMocks
    private StripeIntegration stripeIntegration;

    @Test
    void initPayment_Success() throws StripeException {

        String jsonFilePath = "src/test/resources/StripeResponse_init.json";
        PaymentIntentDto paymentIntentDto = TestHelper.readPaymentIntentFromFile(jsonFilePath);

        ResponseEntity<PaymentIntentDto> output = ResponseEntity.ok().body(paymentIntentDto);
        when(restTemplateStripe.exchange(anyString(), any(HttpMethod.class), any(), eq(PaymentIntentDto.class))).thenReturn(output);

        PaymentIntentDto response = stripeIntegration.initPayment(4L, "usd", "card", "txn");

        assertEquals(paymentIntentDto, response);
    }

    @Test
    void initPayment_Success_Null() throws StripeException {

        ResponseEntity<PaymentIntentDto> output = ResponseEntity.ok().body(null);
        when(restTemplateStripe.exchange(anyString(), any(HttpMethod.class), any(), eq(PaymentIntentDto.class))).thenReturn(output);

        //PaymentIntentDto response = stripeIntegration.initPayment(4L, "usd", "card", "txn");

        assertThrows(CustomException.class, () -> stripeIntegration.initPayment(4L, "usd", "card", "txn"));
    }
}