package com.arhohuttunen;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public Payment pay(Long orderId, String creditCardNumber) {
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        if (order.isPaid()) {
            throw new PaymentException();
        }

        orderRepository.save(order.markPaid());
        return paymentRepository.save(new Payment(order, creditCardNumber));
    }
}
