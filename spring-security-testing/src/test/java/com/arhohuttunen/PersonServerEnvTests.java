package com.arhohuttunen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonServerEnvTests {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WebTestClient webClient;

    @BeforeEach
    void setup() {
        webClient = MockMvcWebTestClient.bindToApplicationContext(context)
                .apply(springSecurity())
                .defaultRequest(get("/").with(csrf()))
                .configureClient()
                .build();
    }

    @Test
    void createPerson() {
        webClient.post().uri("/person")
                .headers(http -> http.setBasicAuth("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"arho\"}")
                .exchange()
                .expectStatus().isCreated();
    }
}
