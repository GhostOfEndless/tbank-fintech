package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApplicationConfig {

    @Bean
    public String kudaGoBaseUrl(@Value("${kudago.base-url}") String uri) {
        return uri;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
