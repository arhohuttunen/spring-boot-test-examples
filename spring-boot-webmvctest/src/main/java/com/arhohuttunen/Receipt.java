package com.arhohuttunen;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Receipt {
    private final LocalDateTime date;
    private final String creditCardNumber;
    private final Double amount;
}
