package com.evangeliakostop.paymentsystem.controllers;

import com.evangeliakostop.paymentsystem.common.utils.CommonService;
import com.evangeliakostop.paymentsystem.common.utils.UniqueIdGenerator;
import com.evangeliakostop.paymentsystem.models.CommonResponse;
import com.evangeliakostop.paymentsystem.models.PaymentInfo;
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

    @Autowired
    public PaymentController(PaymentService paymentService, CommonService commonService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/init", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<Object> initPayment(@RequestBody PaymentRequest request, HttpSession session) {

        String transactionId = UniqueIdGenerator.generateSecureToken();
        session.setAttribute("transactionId", transactionId);
        try {
            PaymentInfo paymentInfo = paymentService.initiatePayment(request, transactionId);

            PaymentResponse response = new PaymentResponse();
            response.setCode(200);
            response.setMessage("Intent Created");
            response.setPaymentInfo(paymentInfo);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse(500, e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "Unknown cause"));
        }
    }
}
