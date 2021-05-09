package com.arhohuttunen;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class OrderRequestTests {
    @Autowired
    private JacksonTester<OrderRequest> jacksonTester;

    @Test
    void deserializeFromCorrectFormat() throws IOException {
        String json = "{\"amount\": \"USD50.00\"}";
        MonetaryAmount expectedAmount = Money.of(50.0, Monetary.getCurrency("USD"));

        OrderRequest orderRequest = jacksonTester.parseObject(json);

        assertThat(orderRequest.getAmount()).isEqualTo(expectedAmount);
    }

    @Test
    void deserializeFromJson() throws IOException {
        MonetaryAmount expectedAmount = Money.of(100.0, Monetary.getCurrency("EUR"));

        OrderRequest orderRequest = jacksonTester.readObject("order.json");

        assertThat(orderRequest.getAmount()).isEqualTo(expectedAmount);
    }
}
