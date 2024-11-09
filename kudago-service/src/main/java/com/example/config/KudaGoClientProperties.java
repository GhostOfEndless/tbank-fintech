package com.example.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties("kudago")
public record KudaGoClientProperties(
    int maxRetries,
    @DurationUnit(ChronoUnit.MILLIS)
    Duration retryDelay,
    int pageSize
) {
}
