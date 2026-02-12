package com.arhohuttunen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MockEnvIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExchangeRateClient exchangeRateClient;

    @Test
    void createOrder() throws Exception {
        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": \"EUR100.0\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql("/unpaid-order.sql")
    void payOrder() throws Exception {
        mockMvc.perform(post("/order/{id}/payment", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"creditCardNumber\": \"4532756279624064\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql("/paid-order.sql")
    void getReceipt() throws Exception {
        CurrencyUnit eur = Monetary.getCurrency("EUR");
        CurrencyUnit usd = Monetary.getCurrency("USD");

        when(exchangeRateClient.getExchangeRate(eur, usd)).thenReturn(BigDecimal.valueOf(0.8412));

        mockMvc.perform(get("/order/{id}/receipt?currency=USD", 1))
                .andExpect(status().isOk());
    }
}
