package com.arhohuttunen.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;

@Data
@NoArgsConstructor
public class OrderRequest {
    @NotNull
    private MonetaryAmount amount;
}
