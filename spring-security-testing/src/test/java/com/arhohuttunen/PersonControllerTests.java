package com.arhohuttunen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
@Import(SecurityConfiguration.class)
class PersonControllerTests {
    @MockBean
    private PersonRepository personRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void getPerson() throws Exception {
        when(personRepository.findById(1L)).thenReturn(Optional.of(new Person(1L, "admin")));

        mockMvc.perform(get("/person/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void cannotGetPersonIfNotAuthorized() throws Exception {
        mockMvc.perform(get("/person/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreatePersons() throws Exception {
        when(personRepository.save(any())).thenReturn(new Person(1L, "arho"));

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"arho\"}")
                        .with(csrf())
                )
                .andExpect(status().isCreated());
    }

    @Test
    @WithAnonymousUser
    void cannotCreatePersonIfNotAuthorized() throws Exception {
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"arho\"}")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void cannotCreatePersonIfNotAnAdmin() throws Exception {
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"arho\"}")
                        .with(csrf())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeletePerson() throws Exception {
        mockMvc.perform(delete("/person/{id}", 1L)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void cannotDeletePersonIfNotAuthorized() throws Exception {
        mockMvc.perform(delete("/person/{id}", 1L)
                        .with(csrf())
                        .with(anonymous())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cannotDeletePersonIfNotAdmin() throws Exception {
        mockMvc.perform(delete("/person/{id}", 1L)
                        .with(csrf())
                        .with(user("user").roles("USER"))
                )
                .andExpect(status().isForbidden());
    }
}
