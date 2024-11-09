package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("async")
public record AsyncProperties(
    int poolSize,
    int capacity
) {
}
