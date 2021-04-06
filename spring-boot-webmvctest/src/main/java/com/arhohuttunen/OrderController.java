package com.arhohuttunen;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/order/{id}/payment")
    public ResponseEntity<PaymentResponse> pay(
            @PathVariable("id") Long orderId,
            @RequestBody @Valid PaymentRequest paymentRequest,
            UriComponentsBuilder uriComponentsBuilder) {

        orderService.pay(orderId, paymentRequest.getCreditCardNumber());
        URI location = uriComponentsBuilder.path("/order/{id}/receipt").buildAndExpand(orderId).toUri();
        return ResponseEntity.created(location).body(null);
    }

    @GetMapping("/order/{id}/receipt")
    public ResponseEntity<ReceiptResponse> getReceipt(@PathVariable("id") Long orderId) {
        Payment payment = orderService.getPayment(orderId);
        ReceiptResponse receipt = new ReceiptResponse(
                payment.getOrder().getDate(),
                payment.getCreditCardNumber(),
                payment.getOrder().getAmount()
        );
        return ResponseEntity.ok().body(receipt);
    }
}
