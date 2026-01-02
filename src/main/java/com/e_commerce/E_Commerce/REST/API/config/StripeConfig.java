package com.e_commerce.E_Commerce.REST.API.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String secretKey;

    @PostConstruct
    public void setUp()
    {
        Stripe.apiKey = secretKey;
    }
}
