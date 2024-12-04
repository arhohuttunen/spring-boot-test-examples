package com.arhohuttunen;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.money.Monetary;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OrderController.class)
class OrderControllerTests {
    @MockitoBean
    private OrderService orderService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getReceiptForOrder() throws Exception {
        givenOrderIsPaid(1L);

        mockMvc.perform(get("/order/{id}/receipt", 1L))
                .andExpect(jsonPath("$.date").isNotEmpty())
                .andExpect(jsonPath("$.creditCardNumber").isNotEmpty())
                .andExpect(jsonPath("$.amount").isNotEmpty());
    }

    private void givenOrderIsPaid(Long orderId) {
        Receipt receipt = new Receipt(
                LocalDateTime.now(),
                "4532756279624064",
                Money.of(100.0, Monetary.getCurrency("EUR")));
        when(orderService.getReceipt(eq(orderId))).thenReturn(receipt);
    }
}
