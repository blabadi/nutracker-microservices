package com.basharallabadi.nutracker.foodcatalog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//test properties can be added here
properties = {
	"spring.cloud.service-registry.auto-registration.enabled=false",
	"eureka.client.enabled=false",
	"eureka.client.serviceUrl.registerWithEureka=false"
})
class FoodCatalogIntegrationTests {

	@Autowired
	ReactiveMongoTemplate mongoTemplate;

	@Autowired
	private WebTestClient webTestClient;


	@AfterEach
	public void cleanup() {
		mongoTemplate.dropCollection(Food.class).block();
	}

	@Test
	void contextLoads() { }


	@Test
	void itShouldFindFood() {
		Food food = new Food();
		food.setName("abc");
		mongoTemplate.save(food).block();

		webTestClient.get()
				.uri("/api/food/search?name=a")
				.exchange()
				.expectBody()
				.consumeWith(
						(content) -> {
							try {
								System.out.println(new String(content.getResponseBodyContent(), "UTF-8"));
							} catch (Exception e) {
								e.printStackTrace();
							}
						})
				.jsonPath("$.[0].name").isEqualTo("abc")
				.jsonPath("$.[0].id").isNotEmpty();
	}

	@Test
	void itShouldSaveFood() {
		Food food = new Food();
		food.setName("abc");

		webTestClient.post()
				.uri("/api/food")
				.body(BodyInserters.fromObject(food))
				.exchange()
				.expectBody()
				.consumeWith(
						(content) -> {
							try {
								System.out.println(new String(content.getResponseBodyContent(), "UTF-8"));
							} catch (Exception e) {
								e.printStackTrace();
							}
						})
				.jsonPath("$.name").isEqualTo("abc")
				.jsonPath("$.id").isNotEmpty();
	}

}

