package com.example.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EnableAsync
@Configuration
public class ApplicationConfig {

    @Bean
    public WebClient kudaGoWebClient(@Value("${kudago.base-url}") String uri) {
        return WebClient.create(uri);
    }

    @Bean
    public WebClient currencyWebClient(@Value("${currency-service.base-url}") String uri) {
        return WebClient.create(uri);
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

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor()  {
        var executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsynchThread-");
        executor.initialize();

        return executor;
    }

    @Bean
    public Duration dataInitSchedule(@Value("${data-loading.interval}") long durationSeconds) {
        return Duration.ofSeconds(durationSeconds);
    }
}
