package com.basharallabadi.nutracker.foodcatalog;

import com.basharallabadi.nutracker.foodcatalog.repo.elastic.FoodElasticSearchRepo;
import com.basharallabadi.nutracker.foodcatalog.repo.mongo.FoodDbRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableReactiveMongoRepositories(basePackageClasses = {FoodDbRepo.class})
@RefreshScope
public class FoodCatalogApplication {
	public static void main(String[] args) {
		SpringApplication.run(FoodCatalogApplication.class, args);
	}

}

// we need two different packages for each repo to avoid conflict in beans creation at startup
@Configuration
@EnableReactiveElasticsearchRepositories(basePackageClasses = {FoodElasticSearchRepo.class})
class elasticConfig extends AbstractReactiveElasticsearchConfiguration {
	@Value("${elasticsearch.server:localhost:9200}")
	private String elasticsearchHost;

	@Override
	public ReactiveElasticsearchClient reactiveElasticsearchClient() {
		return ReactiveRestClients.create(ClientConfiguration.builder().connectedTo(elasticsearchHost).build());
	}
}
