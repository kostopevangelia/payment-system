package com.evangeliakostop.paymentsystem.controllers;

import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.services.PaymentService;
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
@RequestMapping
@Slf4j
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService service;

    @Autowired
    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping(value = "/init", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentResponse> initPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = service.initiatePayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/submit", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentResponse> submitPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = service.submitPayment();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/getInfo", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentResponse> getPaymentInfo(@RequestBody PaymentRequest request) {
        PaymentResponse response = service.getPaymentInfo();
        return ResponseEntity.ok(response);
    }


}
