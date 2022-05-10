package com.arhohuttunen.exchange;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="exchange-rate-api")
public class ExchangeClientProperties {
    private String baseUrl;
    private String apiKey;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
