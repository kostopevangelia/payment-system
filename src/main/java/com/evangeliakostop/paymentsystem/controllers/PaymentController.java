package com.evangeliakostop.paymentsystem.controllers;

import com.evangeliakostop.paymentsystem.common.utils.CommonService;
import com.evangeliakostop.paymentsystem.common.utils.UniqueIdGenerator;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentType;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.models.CommonResponse;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    private final PaymentService paymentService;
    private final CommonService commonService;

    @Autowired
    public PaymentController(PaymentService paymentService, CommonService commonService) {
        this.paymentService = paymentService;
        this.commonService = commonService;
    }

    @PostMapping(value = "/init", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<Object> initPayment(@RequestBody PaymentRequest request, HttpSession session) {

        try {
            PaymentResponse response = paymentService.initiatePayment(request);
            if (response != null) {
                session.setAttribute("transactionId", response.getTransactionId());
                return ResponseEntity.ok().body(response);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse(500, "Error", "Unexpected error"));

        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode())
                    .body(new CommonResponse(e.getErrorCode(), "Bad Request", "400 Bad Request on POST request"));
        }
    }

    @PostMapping(value = "/submit", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentResponse> submitPayment(@RequestBody PaymentRequest request, HttpSession session) {

        String transactionId = (String) session.getAttribute("transactionId");
        if (transactionId == null) {
            transactionId = UniqueIdGenerator.generateSecureToken();
        }

        PaymentResponse response = paymentService.submitPayment(request, transactionId);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setAmount("40000");
        paymentResponse.setPaymentType(PaymentType.CARD);
        paymentResponse.setCurrency("USD");

        return ResponseEntity.ok(paymentResponse);
    }

    @PostMapping(value = "/getInfo", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentResponse> getPaymentInfo(@RequestBody PaymentRequest request, HttpSession session) {

        String transactionId = (String) session.getAttribute("transactionId");
        if (transactionId == null) {
            transactionId = UniqueIdGenerator.generateSecureToken();
        }

        PaymentResponse response = paymentService.getPaymentInfo(request, transactionId);
        return ResponseEntity.ok(response);
    }


}
