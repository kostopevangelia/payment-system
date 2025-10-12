package com.evangeliakostop.playground;

import com.evangeliakostop.playground.utils.CommonService;
import com.evangeliakostop.playground.controllers.PaymentController;
import com.evangeliakostop.playground.dto.PaymentIntentDto;
import com.evangeliakostop.playground.models.PaymentRequest;
import com.evangeliakostop.playground.models.PaymentResponse;
import com.evangeliakostop.playground.services.FraudService;
import com.evangeliakostop.playground.services.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ControllerIT {

    @Mock
    private RestTemplate restTemplateFraudApi;
    @Mock
    private RestTemplate restTemplateStripe;
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private CommonService commonService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private FraudService fraudService;
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

        MvcResult result = mockMvc.perform(post("/payments/init")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        PaymentResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), PaymentResponse.class);

        assertEquals(mockedResponse.getPaymentInfo().getAmount(), response.getPaymentInfo().getAmount());
    }

    @Test
    void completePayment_InitException() throws Exception {
        String jsonRequest = "src/test/resources/PaymentRequest_Invalid.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonRequest);

        mockMvc.perform(post("/payments/init")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andReturn();

    }

    @Test
    void completePayment_ConfirmException() throws Exception {
        String jsonRequest = "src/test/resources/PaymentRequest_Invalid.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonRequest);

        when(restTemplateStripe.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(PaymentIntentDto.class), eq(HashMap.class)))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/payments/init")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andReturn();

    }
}
