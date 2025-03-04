package com.evangeliakostop.paymentsystem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentIntentDto {

    private String id; // Unique identifier for the payment intent

    private Integer amount; // Amount in the smallest currency unit (e.g., cents)

    @JsonProperty("automatic_payment_methods")
    private Map<String, Object> automaticPaymentMethods; // Nullable map for automatic payment settings

    @JsonProperty("client_secret")
    private String clientSecret; // Used for client-side retrieval

    private String currency; // Three-letter ISO currency code

    private String customer; // Nullable customer ID

    private String description; // Optional description

    @JsonProperty("last_payment_error")
    private Map<String, Object> lastPaymentError; // Nullable map for payment errors

    @JsonProperty("latest_charge")
    private String latestCharge; // ID of the latest charge

    private Map<String, String> metadata; // Key-value pairs for additional data

    @JsonProperty("next_action")
    private Map<String, Object> nextAction; // Nullable map for required actions

    @JsonProperty("payment_method")
    private String paymentMethod; // ID of the payment method used

    @JsonProperty("receipt_email")
    private String receiptEmail; // Email for the receipt

    @JsonProperty("setup_future_usage")
    private String setupFutureUsage; // Enum: "off_session" or "on_session"

    private Map<String, Object> shipping; // Nullable shipping details

    @JsonProperty("statement_descriptor")
    private String statementDescriptor; // Custom statement descriptor

    @JsonProperty("statement_descriptor_suffix")
    private String statementDescriptorSuffix; // Additional info for statement descriptor

    private String status; // Status of the payment intent (enum)

}
