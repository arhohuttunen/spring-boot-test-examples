package com.arhohuttunen;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private OrderService orderService;

    @Test
    void payOrder() {
        Order order = new Order(1L, false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.pay(1L, "4532 7562 7962 4064");

        assertThat(order.getPaid()).isTrue();
    }

    @Test
    void cannotPayAlreadyPaidOrder() {
        Order order = new Order(1L, true);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(PaymentException.class, () -> orderService.pay(order.getId(), "4556 6225 7726 8643"));
    }
}
