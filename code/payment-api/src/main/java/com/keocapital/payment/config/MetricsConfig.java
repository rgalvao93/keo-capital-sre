package com.keocapital.payment.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Timer cardAuthorizationTimer(MeterRegistry registry) {
        return Timer.builder("keo.payments.authorization.latency")
            .description("Card authorization latency")
            .tag("service", "payment-api")
            .register(registry);
    }
}