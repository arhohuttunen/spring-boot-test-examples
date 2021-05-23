package com.arhohuttunen;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class TwilioClient {
    private final WebClient webClient;
    private final TwilioClientProperties properties;

    public void sendSms(String from, String to, String message) {
        String baseUrl = properties.getBaseUrl();
        String accountSid = properties.getAccountSid();

        TwilioMessageRequest request = new TwilioMessageRequest(to, from, message);

        webClient.post()
                .uri(baseUrl + "/Accounts/{AccountSid}/Messages.json", accountSid)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TwilioMessageResponse.class)
                .blockOptional()
                .orElseThrow();
    }
}
