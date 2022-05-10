package com.arhohuttunen.payment;

import com.arhohuttunen.order.Order;
import com.arhohuttunen.payment.Payment;
import com.arhohuttunen.payment.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:13.6-alpine://payment"
})
class PaymentRepositoryIntegrationTests {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existingPaymentCanBeFound() {
        Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.0), true);
        Payment payment = new Payment(order, "4532756279624064");

        Long orderId = entityManager.persist(order).getId();
        entityManager.persist(payment);

        Optional<Payment> savedPayment = paymentRepository.findByOrderId(orderId);

        assertThat(savedPayment).isPresent();
        assertThat(savedPayment.get().getOrder().getPaid()).isTrue();
    }

    @Test
    void paymentsAreUniquePerOrder() {
        Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.0), true);
        Payment first = new Payment(order, "4532756279624064");
        Payment second = new Payment(order, "4716327217780406");

        entityManager.persist(order);
        entityManager.persist(first);

        assertThrows(PersistenceException.class, () -> entityManager.persistAndFlush(second));
    }
}
