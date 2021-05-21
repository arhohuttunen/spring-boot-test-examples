package com.arhohuttunen;

import lombok.Data;

@Data
public class TwilioMessageRequest {
    private final String to;
    private final String from;
    private final String body;
}
