package com.keocapital.traffic.service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficSimulatorService {

  private final WebClient.Builder webClientBuilder;

  @Value("${PAYMENT_API_URL:http://localhost:8080}")
  private String paymentApiUrl;

  @Value("${CREDIT_SERVICE_URL:http://localhost:8081}")
  private String creditServiceUrl;

  @Value("${TRAVEL_CARD_SERVICE_URL:http://localhost:8082}")
  private String travelCardServiceUrl;

  @Value("${CROSS_BORDER_FX_URL:http://localhost:8083}")
  private String crossBorderFxUrl;

  private final Random random = new Random();
  private final AtomicInteger requestCount = new AtomicInteger(0);

  private static final List<String> REGIONS = List.of("latam", "canada", "us", "eu");
  private static final List<String> PRODUCTS =
      List.of("keo-travel", "workeo", "cross-border", "supplier-payments");

  @Scheduled(fixedRate = 1000) // 1 request per second
  public void simulateTraffic() {
    int scenario = random.nextInt(100);

    try {
      if (scenario < 40) {
        // 40% - Card authorization
        simulateCardAuthorization();
      } else if (scenario < 60) {
        // 20% - Card creation
        simulateCardCreation();
      } else if (scenario < 75) {
        // 15% - Payment processing
        simulatePaymentProcessing();
      } else if (scenario < 85) {
        // 10% - Credit evaluation
        simulateCreditEvaluation();
      } else if (scenario < 92) {
        // 7% - Working capital disbursement
        simulateWorkingCapitalDisbursement();
      } else if (scenario < 97) {
        // 5% - Cross-border transaction
        simulateCrossBorderTransaction();
      } else {
        // 3% - Travel card creation
        simulateTravelCardCreation();
      }

      int count = requestCount.incrementAndGet();
      if (count % 100 == 0) {
        log.info("Total requests simulated: {}", count);
      }
    } catch (Exception e) {
      log.error("Error simulating traffic: {}", e.getMessage());
    }
  }

  private void simulateCardAuthorization() {
    String region = randomRegion();
    String product = randomProduct();
    double amount = randomAmount(50, 450);

    webClientBuilder
        .build()
        .post()
        .uri(paymentApiUrl + "/api/v1/card/authorize")
        .bodyValue(
            Map.of(
                "customerId", "CUST-" + random.nextInt(1000),
                "region", region,
                "product", product,
                "amount", amount))
        .retrieve()
        .bodyToMono(String.class)
        .subscribe(
            response -> log.debug("Card auth OK"),
            error -> log.warn("Card auth error: {}", error.getMessage()));
  }

  private void simulateCardCreation() {
    String region = randomRegion();
    String product = randomProduct();

    webClientBuilder
        .build()
        .post()
        .uri(paymentApiUrl + "/api/v1/card/create")
        .bodyValue(
            Map.of(
                "customerId", "CUST-" + random.nextInt(1000),
                "region", region,
                "product", product))
        .retrieve()
        .bodyToMono(String.class)
        .subscribe(
            response -> log.debug("Card creation OK"),
            error -> log.warn("Card creation error: {}", error.getMessage()));
  }

  private void simulatePaymentProcessing() {
    String region = randomRegion();
    double amount = randomAmount(100, 900);

    webClientBuilder
        .build()
        .post()
        .uri(paymentApiUrl + "/api/v1/payment/process")
        .bodyValue(
            Map.of(
                "customerId",
                "CUST-" + random.nextInt(1000),
                "region",
                region,
                "product",
                "supplier-payments",
                "amount",
                amount))
        .retrieve()
        .bodyToMono(String.class)
        .subscribe(
            response -> log.debug("Payment OK"),
            error -> log.warn("Payment error: {}", error.getMessage()));
  }

  private void simulateCreditEvaluation() {
    webClientBuilder
        .build()
        .post()
        .uri(creditServiceUrl + "/api/v1/credit/evaluate")
        .bodyValue(
            Map.of(
                "customerId",
                "CUST-" + random.nextInt(1000),
                "region",
                randomRegion(),
                "product",
                "workeo",
                "requestedAmount",
                randomAmount(1000, 9000)))
        .retrieve()
        .bodyToMono(String.class)
        .subscribe(
            response -> log.debug("Credit eval OK"),
            error -> log.warn("Credit eval error: {}", error.getMessage()));
  }

  private void simulateWorkingCapitalDisbursement() {
    webClientBuilder
        .build()
        .post()
        .uri(creditServiceUrl + "/api/v1/working-capital/disburse")
        .bodyValue(
            Map.of(
                "customerId",
                "CUST-" + random.nextInt(1000),
                "region",
                randomRegion(),
                "product",
                "workeo",
                "amount",
                randomAmount(5000, 45000)))
        .retrieve()
        .bodyToMono(String.class)
        .subscribe(
            response -> log.debug("Disbursement OK"),
            error -> log.warn("Disbursement error: {}", error.getMessage()));
  }

  private void simulateCrossBorderTransaction() {
    webClientBuilder
        .build()
        .post()
        .uri(crossBorderFxUrl + "/api/v1/crossborder/transaction")
        .bodyValue(
            Map.of(
                "customerId",
                "CUST-" + random.nextInt(1000),
                "fromRegion",
                randomRegion(),
                "toRegion",
                randomRegion(),
                "amount",
                randomAmount(1000, 9000),
                "currency",
                "USD"))
        .retrieve()
        .bodyToMono(String.class)
        .subscribe(
            response -> log.debug("Cross-border OK"),
            error -> log.warn("Cross-border error: {}", error.getMessage()));
  }

  private void simulateTravelCardCreation() {
    webClientBuilder
        .build()
        .post()
        .uri(travelCardServiceUrl + "/api/v1/travel/card/create")
        .bodyValue(
            Map.of(
                "customerId",
                "CUST-" + random.nextInt(1000),
                "region",
                randomRegion(),
                "product",
                "keo-travel"))
        .retrieve()
        .bodyToMono(String.class)
        .subscribe(
            response -> log.debug("Travel card OK"),
            error -> log.warn("Travel card error: {}", error.getMessage()));
  }

  private String randomRegion() {
    return REGIONS.get(random.nextInt(REGIONS.size()));
  }

  private String randomProduct() {
    return PRODUCTS.get(random.nextInt(PRODUCTS.size()));
  }

  private double randomAmount(double minimum, double range) {
    return Math.round((minimum + random.nextDouble() * range) * 100) / 100.0;
  }
}
