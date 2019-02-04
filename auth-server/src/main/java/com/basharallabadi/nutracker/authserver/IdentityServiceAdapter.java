package com.basharallabadi.nutracker.authserver;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.autoconfigure.CircuitBreakerProperties;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Service
public class IdentityServiceAdapter {

    private WebClient client;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public IdentityServiceAdapter(@Autowired WebClient client, CircuitBreakerRegistry circuitBreakerRegistry, CircuitBreakerProperties circuitBreakerProperties) {
        this.client = client;
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("identity", () -> circuitBreakerProperties.createCircuitBreakerConfig("identity"));
    }


    public Mono<User> byUsername(String name) {
        return client.get().uri("http://identity/api/user/{name}", name)
                .exchange()
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .flatMap(this::handleResponse)
                .onErrorResume(ex -> Mono.empty());
    }

    private Mono<User> handleResponse(ClientResponse response) {
        switch (response.statusCode()) {
            case NOT_FOUND:
                return Mono.empty();
            default:
                return response.statusCode().isError() ?
                        Mono.error(new RuntimeException("identity call failed")) :
                        response.bodyToMono(User.class);
        }
    }
}
