package com.evangeliakostop.paymentsystem.controllers;

import com.evangeliakostop.paymentsystem.models.PaymentHistoryResponse;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.models.PaymentStatusResponse;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        PaymentResponse response = null;
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/status/{transactionId}", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentStatusResponse> getStatus() {
        PaymentStatusResponse response = null;
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/history/{userId}", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentHistoryResponse> getHistory() {
        PaymentHistoryResponse response = null;
        return ResponseEntity.ok(response);
    }
}
