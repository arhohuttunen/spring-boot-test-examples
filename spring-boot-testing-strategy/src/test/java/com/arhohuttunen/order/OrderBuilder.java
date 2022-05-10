package com.arhohuttunen.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderBuilder {
    private final Order order = new Order(1L, LocalDateTime.now(), BigDecimal.valueOf(100.0), false);

    public static OrderBuilder anOrder() {
        return new OrderBuilder();
    }

    public static OrderBuilder aPaidOrder() {
        return new OrderBuilder().isPaid();
    }

    public OrderBuilder withId(Long id) {
        order.setId(id);
        return this;
    }

    public OrderBuilder withDate(LocalDateTime date) {
        order.setDate(date);
        return this;
    }

    public OrderBuilder withAmount(double amount) {
        order.setAmount(BigDecimal.valueOf(amount));
        return this;
    }

    public OrderBuilder isPaid() {
        order.setPaid(true);
        return this;
    }

    public Order build() {
        return order;
    }
}
