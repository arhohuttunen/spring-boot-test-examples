package com.arhohuttunen;

import lombok.Data;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

@Data
public class Receipt {
    private final LocalDateTime date;
    private final String creditCardNumber;
    private final MonetaryAmount amount;
}
