package com.example.client;

import com.example.client.dto.ConvertCurrencyRequest;
import com.example.client.dto.ConvertCurrencyResponse;
import com.example.exception.CurrencyServiceUnavailableException;
import com.example.exception.InvalidCurrencyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyServiceApiClient {

    private final WebClient currencyWebClient;

    public Float convertBudgetToRubles(@NotNull Float budget, @NotNull String currency) {
        var convertRequestBody = ConvertCurrencyRequest.builder()
                .toCurrency("RUB")
                .fromCurrency(currency.toUpperCase())
                .amount(budget)
                .build();

        var response = currencyWebClient.post()
                .uri("/convert")
                .body(BodyInserters.fromValue(convertRequestBody))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, error ->
                        Mono.error(new InvalidCurrencyException(currency)))
                .onStatus(HttpStatusCode::is5xxServerError, error ->
                        Mono.error(new CurrencyServiceUnavailableException()))
                .bodyToMono(ConvertCurrencyResponse.class)
                .block();

        return Objects.requireNonNull(response).convertedAmount();
    }
}
