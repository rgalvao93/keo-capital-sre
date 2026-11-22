package com.keocapital.credit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.export.datadog.DatadogMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DatadogMetricsExportAutoConfiguration.class})
public class CreditServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(CreditServiceApplication.class, args);
  }
}
