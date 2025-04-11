package com.evangeliakostop.paymentsystem.controllers;

import com.evangeliakostop.paymentsystem.TestHelper;
import com.evangeliakostop.paymentsystem.common.utils.CommonService;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.models.PaymentInfo;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentControllerTest {

    @Mock
    private CommonService commonService;
    @Mock
    private HttpSession session;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController controller;

    @Test
    void initPayment_Success() {

        when(session.getId()).thenReturn("mock-session-id");
        doNothing().when(session).setAttribute(anyString(), any());

        String jsonFilePath2 = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonFilePath2);

        String jsonFilePath3 = "src/test/resources/PaymentInfo.json";
        PaymentInfo mockedPaymentInfo = TestHelper.createPaymentInfoFromJson(jsonFilePath3);

        String jsonFilePath4 = "src/test/resources/PaymentResponse.json";
        PaymentResponse mockedResponse = TestHelper.createPaymentResponseFromJson(jsonFilePath4);

        when(paymentService.initiatePayment(any(), anyString(), anyString())).thenReturn(mockedPaymentInfo);

        ResponseEntity<Object> response = controller.initPayment(request, session);

        assertEquals(mockedResponse, response.getBody());

    }

    @Test
    void initPayment_NullResponse() {

        String jsonFilePath2 = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonFilePath2);

        when(paymentService.initiatePayment(any(), anyString(), anyString())).thenReturn(null);

        ResponseEntity<Object> response = controller.initPayment(request, session);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void initPayment_Exception() {

        String jsonFilePath2 = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonFilePath2);

        when(paymentService.initiatePayment(any(), anyString(), anyString())).thenThrow(new CustomException("", "", "", ErrorLevelEnum.APPLICATION_ERROR));

        ResponseEntity<Object> response = controller.initPayment(request, session);

        assertEquals(500, response.getStatusCode().value());
    }
}