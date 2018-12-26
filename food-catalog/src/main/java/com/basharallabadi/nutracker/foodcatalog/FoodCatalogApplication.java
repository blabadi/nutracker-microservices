package com.basharallabadi.nutracker.foodcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableReactiveMongoRepositories()
public class FoodCatalogApplication {
	public static void main(String[] args) {
		SpringApplication.run(FoodCatalogApplication.class, args);
	}
}

