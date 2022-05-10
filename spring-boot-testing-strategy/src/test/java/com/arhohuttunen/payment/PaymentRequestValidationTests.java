package com.arhohuttunen.payment;

import com.arhohuttunen.payment.PaymentRequest;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentRequestValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void creditCardNumberMustNotBeNull() {
        PaymentRequest request = new PaymentRequest(null);

        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void creditCardNumberMustBeValid() {
        String creditCardNumberWithInvalidChecksum = "4532756279624063";
        PaymentRequest request = new PaymentRequest(creditCardNumberWithInvalidChecksum);

        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }
}
