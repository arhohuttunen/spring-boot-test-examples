package com.arhohuttunen;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="twilio-api")
public class TwilioClientProperties {
    private String baseUrl;
    private String accountSid;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }
}
