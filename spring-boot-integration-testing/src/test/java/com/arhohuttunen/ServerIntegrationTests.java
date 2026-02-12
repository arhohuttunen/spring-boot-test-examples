package com.arhohuttunen;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ServerIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private static MockWebServer mockWebServer;
    private WebTestClient webClient;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("exchange-rate-api.base-url", () -> mockWebServer.url("/").url().toString());
    }

    @BeforeAll
    static void setupMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void setUp() {
        this.webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @AfterEach
    void deleteEntities() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void createOrder() {
        webClient.post().uri("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"amount\": \"EUR100.0\"}")
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void payOrder() {
        Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.0), false);
        Long orderId = orderRepository.save(order).getId();

        webClient.post().uri("/order/{id}/payment", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"creditCardNumber\": \"4532756279624064\"}")
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void getReceipt() {
        Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.0), true);
        Payment payment = new Payment(order, "4532756279624064");

        Long orderId = orderRepository.save(order).getId();
        paymentRepository.save(payment);

        mockWebServer.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"conversion_rate\": 0.8412}")
        );

        webClient.get().uri("/order/{id}/receipt?currency=USD", orderId)
                .exchange()
                .expectStatus().isOk();
    }
}
