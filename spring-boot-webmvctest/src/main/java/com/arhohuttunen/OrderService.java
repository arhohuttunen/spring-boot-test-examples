package com.arhohuttunen;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public void pay(Long orderId, String creditCardNumber) {
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        if (order.isPaid()) {
            throw new OrderAlreadyPaid();
        }

        orderRepository.save(order.markPaid());
        paymentRepository.save(new Payment(order, creditCardNumber));
    }

    public Payment getPayment(Long orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(EntityNotFoundException::new);
    }
}
