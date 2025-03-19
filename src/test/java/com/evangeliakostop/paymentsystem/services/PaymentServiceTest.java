package com.evangeliakostop.paymentsystem.services;

import com.evangeliakostop.paymentsystem.TestHelper;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.TransactionType;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceTest {

    @Mock
    private StripeIntegration stripeIntegration;

    @Mock
    private PaymentsDBAccess paymentsDBAccess;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void initiatePaymentSuccess() throws StripeException {
        String jsonFilePath = "src/test/resources/StripeResponse_init.json";
        PaymentIntentDto paymentIntentDto = TestHelper.readPaymentIntentFromFile(jsonFilePath);

        String jsonFilePath2 = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonFilePath2);

        String jsonFilePath3 = "src/test/resources/PaymentResponse.json";
        PaymentResponse mockedResponse = TestHelper.createPaymentResponseFromJson(jsonFilePath3);

        when(stripeIntegration.initPayment(anyLong(), anyString(), anyString(), anyString())).thenReturn(paymentIntentDto);
        doNothing().when(paymentsDBAccess).insertInitTransaction(anyString(), eq(TransactionType.PAYMENT), anyLong(), anyString());

        PaymentResponse response = paymentService.initiatePayment(request);

        assertEquals(mockedResponse, response);

    }

    @Test
    void initiatePayment_NullResponse() throws StripeException {
        String jsonFilePath2 = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonFilePath2);

        when(stripeIntegration.initPayment(anyLong(), anyString(), anyString(), anyString())).thenReturn(null);

        assertThrows(CustomException.class, () -> paymentService.initiatePayment(request));
    }


    @Test
    void initiatePayment() {
    }
}