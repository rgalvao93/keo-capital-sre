package com.keocapital.crossborder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.export.datadog.DatadogMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DatadogMetricsExportAutoConfiguration.class})
public class CrossBorderFxApplication {
  public static void main(String[] args) {
    SpringApplication.run(CrossBorderFxApplication.class, args);
  }
}
