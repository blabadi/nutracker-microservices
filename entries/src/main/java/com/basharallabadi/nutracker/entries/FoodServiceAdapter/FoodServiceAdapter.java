package com.basharallabadi.nutracker.entries.FoodServiceAdapter;

import com.basharallabadi.nutracker.entries.Food;
import com.basharallabadi.nutracker.entries.errors.NotFound;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class FoodServiceAdapter {

    private WebClient client;
    private CircuitBreaker circuitBreaker;

    public FoodServiceAdapter(@Autowired WebClient client) {
        this.client = client;
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50f)
                .waitDurationInOpenState(Duration.ofMillis(10000))
                .ringBufferSizeInHalfOpenState(5)
                .ringBufferSizeInClosedState(5)
                .build();

        this.circuitBreaker = CircuitBreaker.of("food-catalog", circuitBreakerConfig);
    }

    public Mono<Food> byId(String id) {
        return client.get().uri("http://food-catalog/api/food/{id}", id)
                .exchange()
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .flatMap(this::handleResponse)
                .onErrorResume(ex -> Mono.empty());
    }

    private Mono<Food> handleResponse(ClientResponse response) {
        switch (response.statusCode()) {
            case NOT_FOUND:
                return Mono.empty();
            default:
                return response.statusCode().isError() ?
                    Mono.error(new RuntimeException("food service call failed")) :
                        response.bodyToMono(Food.class);
        }
    }
}
