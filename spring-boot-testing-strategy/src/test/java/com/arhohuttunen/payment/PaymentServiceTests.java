package com.arhohuttunen.payment;

import com.arhohuttunen.exchange.ExchangeRateClient;
import com.arhohuttunen.order.Order;
import com.arhohuttunen.order.OrderAlreadyPaid;
import com.arhohuttunen.order.OrderBuilder;
import com.arhohuttunen.order.OrderRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.math.BigDecimal;
import java.util.Optional;

import static com.arhohuttunen.order.OrderBuilder.aPaidOrder;
import static com.arhohuttunen.order.OrderBuilder.anOrder;
import static javax.money.Monetary.getCurrency;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTests {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ExchangeRateClient exchangeRateClient;
    @InjectMocks
    private PaymentService paymentService;

    @Test
    void payOrder() {
        Order order = givenOrder(anOrder());
        givenPaymentWillSucceed();

        Payment payment = paymentService.pay(order.getId(), "4532756279624064");

        assertThat(payment.getOrder().isPaid()).isTrue();
        assertThat(payment.getCreditCardNumber()).isEqualTo("4532756279624064");
    }

    @Test
    void cannotPayAlreadyPaidOrder() {
        Order paidOrder = givenOrder(aPaidOrder());

        assertThrows(OrderAlreadyPaid.class, () -> paymentService.pay(paidOrder.getId(), "4556622577268643"));
    }

    @Test
    void getReceiptInGivenCurrency() {
        Payment payment = givenPaymentForOrder(anOrder().withAmount(100.0));
        givenExchangeRate(1.1, "USD");

        Receipt receipt = paymentService.getReceipt(payment.getOrder().getId(), getCurrency("USD"));

        assertThat(receipt.getAmount()).isEqualTo(Money.of(110.0, "USD"));
    }

    private Order givenOrder(OrderBuilder orderBuilder) {
        Order order = orderBuilder.build();
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        return order;
    }

    private Payment givenPaymentForOrder(OrderBuilder orderBuilder) {
        Order order = orderBuilder.build();
        Payment payment = new Payment(order, "4556622577268643");
        given(paymentRepository.findByOrderId(1L)).willReturn(Optional.of(payment));
        return payment;
    }

    private void givenPaymentWillSucceed() {
        given(paymentRepository.save(any())).will(returnsFirstArg());
    }

    private void givenExchangeRate(double rate, String currency) {
        CurrencyUnit from = Monetary.getCurrency("EUR");
        CurrencyUnit to = Monetary.getCurrency(currency);
        given(exchangeRateClient.getExchangeRate(from, to)).willReturn(BigDecimal.valueOf(rate));
    }
}
