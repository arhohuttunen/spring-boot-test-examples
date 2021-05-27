package com.arhohuttunen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// This is an example of a bad unit test and should not be taken as an advice on how to test WebClient

class TwilioClientUnitTests {
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    private WebClient.RequestHeadersSpec requestHeadersMock;
    private WebClient.RequestBodySpec requestBodyMock;
    private WebClient.ResponseSpec responseMock;
    private WebClient webClientMock;

    @BeforeEach
    void mockWebClient() {
        requestBodyUriMock = mock(WebClient.RequestBodyUriSpec.class);
        requestHeadersMock = mock(WebClient.RequestHeadersSpec.class);
        requestBodyMock = mock(WebClient.RequestBodySpec.class);
        responseMock = mock(WebClient.ResponseSpec.class);
        webClientMock = mock(WebClient.class);
    }

    @Test
    void sendSms() {
        TwilioClientProperties properties = new TwilioClientProperties();
        properties.setBaseUrl("http://localhost");
        properties.setAccountSid("accountSid");

        TwilioClient client = new TwilioClient(webClientMock, properties);

        TwilioMessageResponse response = new TwilioMessageResponse();
        TwilioMessageRequest request = new TwilioMessageRequest("5678", "1234", "message");
        String expectedUri = "http://localhost/Accounts/{AccountSid}/Messages.json";

        when(webClientMock.post()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri(eq(expectedUri), eq("accountSid"))).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(eq(request))).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(TwilioMessageResponse.class)).thenReturn(Mono.just(response));

        assertDoesNotThrow(() -> client.sendSms("1234", "5678", "message"));
    }
}
