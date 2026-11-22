package com.keocapital.travel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.export.datadog.DatadogMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DatadogMetricsExportAutoConfiguration.class})
public class TravelCardServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TravelCardServiceApplication.class, args);
    }
}