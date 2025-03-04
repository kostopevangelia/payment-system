package com.evangeliakostop.paymentsystem.controllers;

import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import com.evangeliakostop.paymentsystem.utils.UniqueIdGenerator;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payments")
@Slf4j
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService service;

    @Autowired
    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping(value = "/init", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentResponse> initPayment(@RequestBody PaymentRequest request, HttpSession session) {
        PaymentResponse response = service.initiatePayment(request);
        session.setAttribute("transactionId", response.getTransactionId());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/submit", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentResponse> submitPayment(@RequestBody PaymentRequest request, HttpSession session) {

        String transactionId = (String) session.getAttribute("transactionId");
        if (transactionId == null) {
            transactionId = UniqueIdGenerator.generateSecureToken();
        }

        PaymentResponse response = service.submitPayment(request, transactionId);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setAmount("40000");
        paymentResponse.setPaymentType("Card");
        paymentResponse.setCurrency("USD");
        return ResponseEntity.ok(paymentResponse);
    }

    @PostMapping(value = "/getInfo", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentResponse> getPaymentInfo(@RequestBody PaymentRequest request, HttpSession session) {

        String transactionId = (String) session.getAttribute("transactionId");
        if (transactionId == null) {
            transactionId = UniqueIdGenerator.generateSecureToken();
        }

        PaymentResponse response = service.getPaymentInfo(request, transactionId);
        return ResponseEntity.ok(response);
    }


}
