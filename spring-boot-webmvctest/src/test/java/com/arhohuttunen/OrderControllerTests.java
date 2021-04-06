package com.arhohuttunen;

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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
        givenPaymentSucceeds(1L);

        mockMvc.perform(post("/order/{id}/payment", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"creditCardNumber\": \"4532756279624064\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/order/1/receipt"));
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
        givenPaymentWithIdAlreadyExists(1L);

        mockMvc.perform(post("/order/{id}/payment", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"creditCardNumber\": \"4532756279624064\"}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void receiptCanBeFound() throws Exception {
        givenOrderIsPaid(1L);

        mockMvc.perform(get("/order/{id}/receipt", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getReceiptForOrder() throws Exception {
        Order order = new Order(1L, LocalDateTime.now(), 100.0, false);
        Payment payment = new Payment(1000L, order, "4532756279624064");

        givenOrderIsPaid(payment);

        mockMvc.perform(get("/order/{id}/receipt", 1L))
                .andExpect(jsonPath("$.date").isNotEmpty())
                .andExpect(jsonPath("$.creditCardNumber").value("4532756279624064"))
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    private void givenPaymentSucceeds(Long orderId) {
        doNothing().when(orderService).pay(eq(orderId), any());
    }

    private void givenPaymentWithIdAlreadyExists(Long orderId) {
        doThrow(OrderAlreadyPaid.class).when(orderService).pay(eq(orderId), any());
    }

    private void givenOrderIsPaid(Long orderId) {
        Order order = new Order(orderId, LocalDateTime.now(), 100.0, false);
        Payment payment = new Payment(1000L, order, "4532756279624064");
        givenOrderIsPaid(payment);
    }

    private void givenOrderIsPaid(Payment payment) {
        doReturn(payment).when(orderService).getPayment(eq(payment.getOrder().getId()));
    }
}
