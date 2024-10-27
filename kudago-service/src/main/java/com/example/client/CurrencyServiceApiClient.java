package com.example.client;

import com.example.client.dto.ConvertCurrencyRequest;
import com.example.client.dto.ConvertCurrencyResponse;
import com.example.exception.ServiceUnavailableException;
import com.example.exception.InvalidCurrencyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyServiceApiClient {

    private final WebClient currencyWebClient;

    public Mono<Float> convertBudgetToRublesReactive(Float budget, String currency) {
        return currencyWebClient.post()
                .uri("/convert")
                .body(BodyInserters.fromValue(
                        makeConvertRequest(budget, currency)))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, error ->
                        Mono.error(new InvalidCurrencyException(currency)))
                .onStatus(HttpStatusCode::is5xxServerError, error ->
                        Mono.error(new ServiceUnavailableException()))
                .bodyToMono(ConvertCurrencyResponse.class)
                .onErrorResume(WebClientRequestException.class,
                        error -> Mono.error(new ServiceUnavailableException()))
                .map(ConvertCurrencyResponse::convertedAmount);
    }

    public CompletableFuture<Float> convertBudgetToRublesFuture(Float budget, String currency) {
        return currencyWebClient.post()
                .uri("/convert")
                .body(BodyInserters.fromValue(
                        makeConvertRequest(budget, currency)))
                .retrieve()
                .bodyToMono(ConvertCurrencyResponse.class)
                .map(ConvertCurrencyResponse::convertedAmount)
                .toFuture()
                .exceptionally(throwable -> {
                    if (throwable.getCause() instanceof WebClientResponseException.BadRequest) {
                        throw new InvalidCurrencyException(currency);
                    }
                    throw new ServiceUnavailableException();
                });
    }

    private ConvertCurrencyRequest makeConvertRequest(Float budget, String currency) {
        return ConvertCurrencyRequest.builder()
                .toCurrency("RUB")
                .fromCurrency(currency.toUpperCase())
                .amount(budget)
                .build();
    }
}
