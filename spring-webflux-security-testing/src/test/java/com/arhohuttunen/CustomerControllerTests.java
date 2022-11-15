package com.arhohuttunen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@WebFluxTest(CustomerController.class)
@Import(SecurityConfiguration.class)
class CustomerControllerTests {
    @MockBean
    private CustomerRepository customerRepository;

    @Autowired
    private WebTestClient webClient;

    @Test
    @WithMockUser
    void getCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Mono.just(new Customer(1L, "John", "Doe")));

        webClient.get().uri("/customer/{id}", 1)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithAnonymousUser
    void cannotGetCustomerIfNotAuthorized() {
        webClient.get().uri("/customer/{id}", 1)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateCustomers() {
        when(customerRepository.save(any())).thenReturn(Mono.just(new Customer(1L, "John", "Doe")));

        webClient.mutateWith(csrf())
                .post().uri("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"firstName\": \"John\", \"lastName\": \"Doe\"}")
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    @WithAnonymousUser
    void cannotCreateCustomerIfNotAuthorized() {
        webClient.mutateWith(csrf())
                .post().uri("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"firstName\": \"John\", \"lastName\": \"Doe\"}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser
    void cannotCreateCustomerIfNotAnAdmin() throws Exception {
        webClient.mutateWith(csrf())
                .post().uri("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"firstName\": \"John\", \"lastName\": \"Doe\"}")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void adminCanDeleteCustomer() {
        when(customerRepository.deleteById(1L)).thenReturn(Mono.empty());

        webClient.mutateWith(csrf())
                .mutateWith(mockUser().roles("ADMIN"))
                .delete().uri("/customer/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void cannotDeleteCustomerIfNotAuthorized() throws Exception {
        webClient.mutateWith(csrf())
                .delete().uri("/customer/{id}", 1)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void cannotDeleteCustomerIfNotAdmin() throws Exception {
        webClient.mutateWith(csrf())
                .mutateWith(mockUser())
                .delete().uri("/customer/{id}", 1)
                .exchange()
                .expectStatus().isForbidden();
    }
}
