package com.ecommerce.api.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.ecommerce.api.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final WebhookService webhookService;

    @Value("${stripe.webhook-secret:#{null}}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody byte[] payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        // Step 1: Verify signature
        try {
            if (webhookSecret == null || webhookSecret.isBlank()) {
                log.warn("Stripe webhook secret not configured. Skipping signature verification.");
                event = com.stripe.model.Event.GSON.fromJson(new String(payload), Event.class);
            } else {
                event = Webhook.constructEvent(
                        new String(payload),
                        sigHeader,
                        webhookSecret
                );
            }
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature received: {}", e.getMessage());
            return ResponseEntity.status(400).body("Invalid signature");
        } catch (Exception e) {
            log.error("Webhook payload parsing failed: {}", e.getMessage());
            return ResponseEntity.status(400).body("Invalid payload");
        }

        // Step 2: Delegate to service (return 200 immediately to Stripe)
        try {
            webhookService.processStripeEvent(event);
        } catch (Exception e) {
            log.error("Error processing webhook event: type={}, id={}", event.getType(), event.getId(), e);
            // Still return 200 to Stripe to prevent retries
        }

        return ResponseEntity.ok("Received");
    }
}

