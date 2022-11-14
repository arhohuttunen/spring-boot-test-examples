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

@WebMvcTest(CustomerController.class)
@Import(SecurityConfiguration.class)
class CustomerControllerTests {
    @MockBean
    private CustomerRepository customerRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void getCustomer() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John", "Doe")));

        mockMvc.perform(get("/customer/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void cannotGetCustomerIfNotAuthorized() throws Exception {
        mockMvc.perform(get("/customer/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateCustomers() throws Exception {
        when(customerRepository.save(any())).thenReturn(new Customer(1L, "John", "Doe"));

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\"}")
                        .with(csrf())
                )
                .andExpect(status().isCreated());
    }

    @Test
    @WithAnonymousUser
    void cannotCreateCustomerIfNotAuthorized() throws Exception {
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\"}")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void cannotCreateCustomerIfNotAnAdmin() throws Exception {
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\"}")
                        .with(csrf())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/customer/{id}", 1L)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void cannotDeleteCustomerIfNotAuthorized() throws Exception {
        mockMvc.perform(delete("/customer/{id}", 1L)
                        .with(csrf())
                        .with(anonymous())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cannotDeleteCustomerIfNotAdmin() throws Exception {
        mockMvc.perform(delete("/customer/{id}", 1L)
                        .with(csrf())
                        .with(user("user").roles("USER"))
                )
                .andExpect(status().isForbidden());
    }
}
