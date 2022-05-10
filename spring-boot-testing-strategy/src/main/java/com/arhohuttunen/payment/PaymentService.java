package com.arhohuttunen.payment;

import com.arhohuttunen.exchange.ExchangeRateClient;
import com.arhohuttunen.order.Order;
import com.arhohuttunen.order.OrderAlreadyPaid;
import com.arhohuttunen.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ExchangeRateClient exchangeRateClient;

    public Payment pay(Long orderId, String creditCardNumber) {
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        if (order.isPaid()) {
            throw new OrderAlreadyPaid();
        }

        orderRepository.save(order.markPaid());
        return paymentRepository.save(new Payment(order, creditCardNumber));
    }

    public Receipt getReceipt(Long orderId, CurrencyUnit currency) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(EntityNotFoundException::new);

        BigDecimal rate = exchangeRateClient.getExchangeRate(Monetary.getCurrency("EUR"), currency);

        BigDecimal amount = payment.getOrder().getAmount();
        MonetaryAmount convertedAmount = Money.of(amount.multiply(rate), currency);

        return new Receipt(payment.getOrder().getDate(), payment.getCreditCardNumber(), convertedAmount);
    }
}
