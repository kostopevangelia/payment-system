package com.evangeliakostop.paymentsystem.services;

import com.evangeliakostop.paymentsystem.TestHelper;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.integrations.PaymentMsIntegration;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.PaymentInfo;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
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
class PaymentInfoServiceTest {

    @Mock
    private PaymentMsIntegration paymentMsIntegration;

    @Mock
    private StripeIntegration stripeIntegration;

    @Mock
    private PaymentsDBAccess paymentsDBAccess;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void initiatePaymentSuccess() throws StripeException {
        String jsonFilePath = "src/test/resources/StripeResponse_init_status_succeeded.json";
        PaymentIntentDto paymentIntentDto = TestHelper.readPaymentIntentFromFile(jsonFilePath);

        String jsonFilePath2 = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonFilePath2);

        String jsonFilePath3 = "src/test/resources/PaymentInfo.json";
        PaymentInfo mockedResponse = TestHelper.createPaymentInfoFromJson(jsonFilePath3);

        when(stripeIntegration.initPayment(anyLong(), anyString(), anyString(), anyString())).thenReturn(paymentIntentDto);
        doNothing().when(paymentsDBAccess).insertInitTransaction(anyString(), eq(TransactionType.PAYMENT), anyLong(), anyString());

        PaymentInfo paymentInfo = paymentService.initiatePayment(request, "transactionId", "sessionId");

        assertEquals(mockedResponse, paymentInfo);

    }

    @Test
    void initiatePayment_NullResponse() throws StripeException {
        String jsonFilePath2 = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonFilePath2);

        when(stripeIntegration.initPayment(anyLong(), anyString(), anyString(), anyString())).thenReturn(null);

        assertThrows(CustomException.class, () -> paymentService.initiatePayment(request,"transactionId", "sessionId"));
    }


    @Test
    void initiatePayment_StatusNotSucceeded() {
        String jsonFilePath = "src/test/resources/StripeResponse_init_status_not_succeeded.json";
        PaymentIntentDto paymentIntentDto = TestHelper.readPaymentIntentFromFile(jsonFilePath);

        String jsonFilePath2 = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonFilePath2);

        String jsonFilePath3 = "src/test/resources/PaymentInfo.json";
        PaymentInfo mockedResponse = TestHelper.createPaymentInfoFromJson(jsonFilePath3);

        when(stripeIntegration.initPayment(anyLong(), anyString(), anyString(), anyString())).thenReturn(paymentIntentDto);
        when(paymentMsIntegration.confirmPayment(any(), any(), anyString())).thenReturn(mockedResponse);
        doNothing().when(paymentsDBAccess).insertInitTransaction(anyString(), eq(TransactionType.PAYMENT), anyLong(), anyString());

        PaymentInfo paymentInfo = paymentService.initiatePayment(request, "transactionId", "sessionId");

        assertEquals(mockedResponse, paymentInfo);
    }
}