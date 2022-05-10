package com.arhohuttunen.exchange;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeResponse {
    @JsonAlias("conversion_rate")
    private BigDecimal conversionRate;
    private String result;
    @JsonAlias("error-type")
    private String errorType;
}
