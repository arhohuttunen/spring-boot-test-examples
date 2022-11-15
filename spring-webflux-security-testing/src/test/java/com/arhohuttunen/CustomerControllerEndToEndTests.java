package com.arhohuttunen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@SpringBootTest
public class CustomerControllerEndToEndTests {
    @Autowired
    private ApplicationContext context;

    private WebTestClient webClient;

    @BeforeEach
    void setup() {
        webClient = WebTestClient.bindToApplicationContext(context)
                .apply(springSecurity())
                .configureClient()
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCustomer() {
        webClient.mutateWith(csrf())
                .post().uri("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"firstName\": \"John\", \"lastName\": \"Doe\"}")
                .exchange()
                .expectStatus().isCreated();
    }
}
