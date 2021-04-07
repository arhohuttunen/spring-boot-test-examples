package com.arhohuttunen;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.money.Monetary;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ReceiptResponseTests {
    @Autowired
    private JacksonTester<ReceiptResponse> jacksonTester;

    @Test
    void serializeInCorrectFormat() throws IOException {
        ReceiptResponse receipt = new ReceiptResponse(
                LocalDateTime.of(2021, 3, 30, 17, 5),
                "4532756279624064",
                Money.of(100.0, Monetary.getCurrency("EUR")));

        JsonContent<ReceiptResponse> json = jacksonTester.write(receipt);

        assertThat(json).extractingJsonPathStringValue("$.date").isEqualTo("30.03.2021 17:05");
        assertThat(json).extractingJsonPathStringValue("$.creditCardNumber").isEqualTo("4532756279624064");
        assertThat(json).extractingJsonPathStringValue("$.amount").isEqualTo("EUR100.00");
    }
}
