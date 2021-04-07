package com.arhohuttunen;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReceiptResponse {
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private final LocalDateTime date;
    private final String creditCardNumber;
    private final MonetaryAmount amount;
}
