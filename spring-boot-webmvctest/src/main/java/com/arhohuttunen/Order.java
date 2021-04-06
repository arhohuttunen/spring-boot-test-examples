package com.arhohuttunen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime date;
    private Double amount;
    private Boolean paid;

    public boolean isPaid() {
        return paid;
    }

    public Order markPaid() {
        paid = true;
        return this;
    }
}
