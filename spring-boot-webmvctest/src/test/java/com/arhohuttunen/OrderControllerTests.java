package com.arhohuttunen;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTests {
    @MockBean
    private OrderService orderService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void payOrder() throws Exception {
        Order order = new Order(1L, LocalDateTime.now(), 100.0, false);
        Payment payment = new Payment(1000L, order, "4532756279624064");

        when(orderService.pay(eq(1L), eq("4532756279624064"))).thenReturn(payment);

        mockMvc.perform(post("/order/{id}/payment", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"creditCardNumber\": \"4532756279624064\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/order/1/receipt"));
    }

    @Test
    void paymentFailsWhenOrderIsNotFound() throws Exception {
        when(orderService.pay(eq(1L), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/order/{id}/payment", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"creditCardNumber\": \"4532756279624064\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void paymentFailsWhenCreditCardNumberNotGiven() throws Exception {
        mockMvc.perform(post("/order/{id}/payment", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cannotPayAlreadyPaidOrder() throws Exception {
        when(orderService.pay(eq(1L), any())).thenThrow(OrderAlreadyPaid.class);

        mockMvc.perform(post("/order/{id}/payment", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"creditCardNumber\": \"4532756279624064\"}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void receiptCanBeFound() throws Exception {
        Receipt receipt = new Receipt(LocalDateTime.now(), "4532756279624064", 100.0);

        when(orderService.getReceipt(eq(1L))).thenReturn(receipt);

        mockMvc.perform(get("/order/{id}/receipt", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getReceiptForOrder() throws Exception {
        Receipt receipt = new Receipt(
                LocalDateTime.now(),
                "4532756279624064",
                100.0);

        when(orderService.getReceipt(eq(1L))).thenReturn(receipt);

        mockMvc.perform(get("/order/{id}/receipt", 1L))
                .andExpect(jsonPath("$.date").isNotEmpty())
                .andExpect(jsonPath("$.creditCardNumber").value("4532756279624064"))
                .andExpect(jsonPath("$.amount").value(100.0));
    }
}
