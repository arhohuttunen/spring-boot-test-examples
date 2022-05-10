package com.arhohuttunen.payment;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class PaymentRequestBuilder {
    private final MockMvc mockMvc;

    PaymentRequestBuilder(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions pay(Long orderId) throws Exception {
        return mockMvc.perform(post("/order/{id}/payment", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"creditCardNumber\": \"4532756279624064\"}"));
    }

    public ResultActions payWithoutCreditCardNumber(Long orderId) throws Exception {
        return mockMvc.perform(post("/order/{id}/payment", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));
    }

    public ResultActions getReceipt(Long orderId) throws Exception {
        return mockMvc.perform(get("/order/{id}/receipt", orderId));
    }
}
