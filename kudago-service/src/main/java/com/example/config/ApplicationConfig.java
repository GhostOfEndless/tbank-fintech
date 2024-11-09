package com.example.config;

import com.example.exception.entity.UserNotFoundException;
import com.example.repository.security.AppUserRepository;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AppUserRepository appUserRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return login -> appUserRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public WebClient kudaGoWebClient(@Value("${kudago.base-url}") String uri) {
        return WebClient.create(uri);
    }

    @Bean
    public WebClient currencyWebClient(@Value("${currency-service.base-url}") String uri) {
        return WebClient.create(uri);
    }

    @Bean(name = "dataLoaderThreadPool")
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
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();

        return executor;
    }

    @Bean
    public Duration dataInitSchedule(@Value("${data-loading.interval}") long durationSeconds) {
        return Duration.ofSeconds(durationSeconds);
    }

    @Bean
    public Duration dataInitTimeout(@Value("${data-loading.timeout:60}") long timeoutSeconds) {
        return Duration.ofSeconds(timeoutSeconds);
    }
}
