package com.keocapital.traffic.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class TrafficSimulatorServiceTest {

  private TrafficSimulatorService service;

  @BeforeEach
  void setUp() {
    ExchangeFunction exchangeFunction =
        request ->
            Mono.just(
                ClientResponse.create(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body("{}")
                    .build());
    WebClient webClient = WebClient.builder().exchangeFunction(exchangeFunction).build();

    WebClient.Builder builder = mock(WebClient.Builder.class);
    when(builder.build()).thenReturn(webClient);

    service = new TrafficSimulatorService(builder);
    ReflectionTestUtils.setField(service, "paymentApiUrl", "http://localhost:8080");
    ReflectionTestUtils.setField(service, "creditServiceUrl", "http://localhost:8081");
    ReflectionTestUtils.setField(service, "travelCardServiceUrl", "http://localhost:8082");
    ReflectionTestUtils.setField(service, "crossBorderFxUrl", "http://localhost:8083");
  }

  @Test
  void simulateTrafficExercisesAllScenarios() {
    // O cenário é escolhido aleatoriamente; várias iterações cobrem todos os ramos.
    for (int i = 0; i < 500; i++) {
      service.simulateTraffic();
    }
  }
}
