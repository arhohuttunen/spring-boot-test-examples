package com.arhohuttunen;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    @NonNull
    private Order order;
    @NonNull
    private String creditCardNumber;
}
