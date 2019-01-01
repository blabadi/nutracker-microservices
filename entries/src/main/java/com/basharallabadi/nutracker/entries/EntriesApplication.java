package com.basharallabadi.nutracker.entries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableDiscoveryClient
@RefreshScope
public class EntriesApplication {

	// this is needed to enable the webclient to use Eurka and Ribbon since @LoadBalanced not supported yet.
	@Bean
	WebClient client(LoadBalancerExchangeFilterFunction eff) {
		return WebClient.builder().filter(logResponse()).filter(eff).build();
	}

	private ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(res -> {
			System.out.println("Response status code  " + res.statusCode());
			return Mono.just(res);
		});
	}

	public static void main(String[] args) {
		SpringApplication.run(EntriesApplication.class, args);
	}



}

