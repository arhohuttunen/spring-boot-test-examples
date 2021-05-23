package com.arhohuttunen;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TwilioMessageResponse {
    @JsonAlias("error_code")
    private String errorCode;
    @JsonAlias("error_message")
    private String errorMessage;
}
