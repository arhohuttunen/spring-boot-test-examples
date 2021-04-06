package com.arhohuttunen;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReceiptResponse {
    private final LocalDateTime date;
    private final String creditCardNumber;
    private final Double amount;
}
