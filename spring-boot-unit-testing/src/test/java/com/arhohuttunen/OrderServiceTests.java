package com.arhohuttunen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OrderServiceTests {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    @Test
    void payOrder() {
        Order order = new Order(1L, false);
        orderRepository.save(order);

        orderService.pay(1L, "4532 7562 7962 4064");

        Order savedOrder = orderRepository.findById(1L).get();
        assertThat(savedOrder.getPaid()).isTrue();
    }

    @Test
    void cannotPayAlreadyPaidOrder() {
        Order order = new Order(2L, true);
        orderRepository.save(order);

        assertThrows(PaymentException.class, () -> orderService.pay(2L, "4556 6225 7726 8643"));
    }
}
