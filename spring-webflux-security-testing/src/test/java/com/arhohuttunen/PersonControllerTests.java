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

@WebFluxTest(PersonController.class)
@Import(SecurityConfiguration.class)
class PersonControllerTests {
    @MockBean
    private PersonRepository personRepository;

    @Autowired
    private WebTestClient webClient;

    @Test
    @WithMockUser
    void getPerson() {
        when(personRepository.findById(1L)).thenReturn(Mono.just(new Person(1L, "admin")));

        webClient.get().uri("/person/{id}", 1)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithAnonymousUser
    void cannotGetPersonIfNotAuthorized() {
        webClient.get().uri("/person/{id}", 1)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreatePersons() {
        when(personRepository.save(any())).thenReturn(Mono.just(new Person(1L, "arho")));

        webClient.mutateWith(csrf())
                .post().uri("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"arho\"}")
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    @WithAnonymousUser
    void cannotCreatePersonIfNotAuthorized() {
        webClient.mutateWith(csrf())
                .post().uri("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"arho\"}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser
    void cannotCreatePersonIfNotAnAdmin() throws Exception {
        webClient.mutateWith(csrf())
                .post().uri("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"arho\"}")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void adminCanDeletePerson() {
        when(personRepository.deleteById(1L)).thenReturn(Mono.empty());

        webClient.mutateWith(csrf())
                .mutateWith(mockUser().roles("ADMIN"))
                .delete().uri("/person/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void cannotDeletePersonIfNotAuthorized() throws Exception {
        webClient.mutateWith(csrf())
                .delete().uri("/person/{id}", 1)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void cannotDeletePersonIfNotAdmin() throws Exception {
        webClient.mutateWith(csrf())
                .mutateWith(mockUser())
                .delete().uri("/person/{id}", 1)
                .exchange()
                .expectStatus().isForbidden();
    }
}
