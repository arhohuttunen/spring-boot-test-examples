package com.arhohuttunen.payment;

import com.arhohuttunen.order.Order;
import com.arhohuttunen.order.OrderAlreadyPaid;
import jakarta.persistence.EntityNotFoundException;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerIntegrationTests {
    @MockBean
    private PaymentService paymentService;
    @Autowired
    private MockMvc mockMvc;

    private PaymentRequestBuilder paymentEndpoint;

    @BeforeEach
    void before() {
        paymentEndpoint = new PaymentRequestBuilder(mockMvc);
    }

    @Test
    void payOrder() throws Exception {
        givenPaymentWillSucceed(1L);

        paymentEndpoint.pay(1L)
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/order/1/receipt"));
    }

    @Test
    void paymentFailsWhenOrderIsNotFound() throws Exception {
        givenOrderIsNotFound(1L);

        paymentEndpoint.pay(1L)
                .andExpect(status().isNotFound());
    }

    @Test
    void paymentFailsWhenCreditCardNumberNotGiven() throws Exception {
        paymentEndpoint.payWithoutCreditCardNumber(1L)
                .andExpect(status().isBadRequest());
    }

    @Test
    void cannotPayAlreadyPaidOrder() throws Exception {
        givenOrderIsPaid(1L);

        paymentEndpoint.pay(1L)
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void receiptCanBeFound() throws Exception {
        Long orderId = givenReceipt();

        paymentEndpoint.getReceipt(orderId)
                .andExpect(status().isOk());
    }

    @Test
    void getReceiptForOrder() throws Exception {
        Long orderId = givenReceipt();

        paymentEndpoint.getReceipt(orderId)
                .andExpect(jsonPath("$.date").isNotEmpty())
                .andExpect(jsonPath("$.creditCardNumber").isNotEmpty())
                .andExpect(jsonPath("$.amount").isNotEmpty());
    }

    private void givenPaymentWillSucceed(Long orderId) {
        Order order = new Order(orderId, LocalDateTime.now(), BigDecimal.valueOf(100.0), false);
        Payment payment = new Payment(1000L, order, "4532756279624064");
        given(paymentService.pay(eq(orderId), eq("4532756279624064"))).willReturn(payment);
    }

    private void givenOrderIsNotFound(Long orderId) {
        given(paymentService.pay(eq(orderId), any())).willThrow(EntityNotFoundException.class);
    }

    private void givenOrderIsPaid(Long orderId) {
        given(paymentService.pay(eq(orderId), any())).willThrow(OrderAlreadyPaid.class);
    }

    private Long givenReceipt() {
        Receipt receipt = new Receipt(LocalDateTime.now(), "4532756279624064", Money.of(100.0, "EUR"));
        given(paymentService.getReceipt(eq(1L), eq(Monetary.getCurrency("EUR")))).willReturn(receipt);
        return 1L;
    }
}
