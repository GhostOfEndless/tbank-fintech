package com.example.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ApplicationConfig {

    @Bean
    public String kudaGoBaseUrl(@Value("${kudago.base-url}") String uri) {
        return uri;
    }

    @Bean
    public RestClient categoriesRestClient() {
        return RestClient.create();
    }

    @Bean
    public ExecutorService dataLoaderThreadPool(@Value("${data-loading.threads}") int numOfThreads) {
        var namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("data-loader-%d").build();
        return Executors.newFixedThreadPool(numOfThreads, namedThreadFactory);
    }

    @Bean
    public ScheduledExecutorService scheduledDataInitPool() {
        var namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("init-scheduler-%d").build();
        return Executors.newScheduledThreadPool(1, namedThreadFactory);
    }

    @Bean
    public Duration dataInitSchedule(@Value("${data-loading.interval}") long durationSeconds) {
        return Duration.ofSeconds(durationSeconds);
    }
}
