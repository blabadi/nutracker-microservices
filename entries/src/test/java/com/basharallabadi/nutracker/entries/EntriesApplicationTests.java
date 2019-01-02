package com.basharallabadi.nutracker.entries;

import com.basharallabadi.nutracker.entries.FoodServiceAdapter.FoodServiceAdapter;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"spring.cloud.service-registry.auto-registration.enabled=false",
				"eureka.client.enabled=false",
				"eureka.client.serviceUrl.registerWithEureka=false"
		}
)
public class EntriesApplicationTests {

    @MockBean
    FoodServiceAdapter foodServiceAdapter;

	@Autowired
	ReactiveMongoTemplate mongoTemplate;

	@Autowired
	private WebTestClient webTestClient;


	@AfterEach
	public void cleanup() {
		mongoTemplate.dropCollection(Entry.class).block();
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void itShouldGetEntriesForPreiod() {
		Entry entryInPeriod1 = Entry.builder()
				.amount(1)
				.createdAt(LocalDateTime.parse("2018-12-03T10:12:00"))
				.owner("tester")
				.food(Food.builder().brand("Great Value").calories(100).carbs(20).fat(3).protein(1).name("bar").id("1234avc").unit("100 g").build())
				.build();

		Entry entryInPeriod2 = Entry.builder()
				.amount(2)
				.createdAt(LocalDateTime.parse("2018-12-03T09:05:02"))
				.owner("tester")
				.food(Food.builder().brand("PC").calories(40).carbs(2).fat(4).protein(6).name("eggs").id("1234avc").unit("egg").build())
				.build();

		Entry entryOutsidePeriod = Entry.builder()
				.amount(1)
				.createdAt(LocalDateTime.parse("2018-12-05T10:12:00"))
				.owner("tester")
				.food(Food.builder().brand("Great Value").calories(100).carbs(20).fat(3).protein(1).name("bar").id("1234avc").unit("100 g").build())
				.build();

		Entry entryForAnotherUser = Entry.builder()
				.amount(1)
				.createdAt(LocalDateTime.parse("2018-12-03T10:12:00"))
				.owner("tester2")
				.food(Food.builder().brand("Great Value").calories(100).carbs(20).fat(3).protein(1).name("bar").id("1234avc").unit("100 g").build())
				.build();

		mongoTemplate.insertAll(List.of(entryInPeriod1, entryInPeriod2, entryOutsidePeriod, entryForAnotherUser)).blockLast();

		webTestClient.get()
				.uri("/api/entry?user=tester&from=20181203&to=20181203")
				.exchange()
				.expectBody()
				.consumeWith(
						(content) -> {
							try {
								System.out.println(new String(content.getResponseBodyContent(), "UTF-8"));
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						})
				.jsonPath("$.length()").isEqualTo(2)
				.jsonPath("$.[0].createdAt").isEqualTo("2018-12-03T10:12:00")
				.jsonPath("$.[0].food.name").isEqualTo("bar")
				.jsonPath("$.[0].amount").isEqualTo(1)
				.jsonPath("$.[0].id").isNotEmpty()

				.jsonPath("$.[1].createdAt").isEqualTo("2018-12-03T09:05:02")
				.jsonPath("$.[1].food.name").isEqualTo("eggs")
				.jsonPath("$.[1].amount").isEqualTo(2)
				.jsonPath("$.[1].id").isNotEmpty();
	}

	@Test
	public void itShouldSaveEntry() {
        Mockito.when(foodServiceAdapter.byId("1234avc")).thenReturn(Mono.just(Food.builder()
                .id("1234avc").build()));
		Entry entry = Entry.builder()
				.amount(1)
				.createdAt(LocalDateTime.parse("2018-12-03T10:12:00"))
				.owner("tester")
				.food(Food.builder().brand("Great Value").calories(100).carbs(20).fat(3).protein(1).name("bar").id("1234avc").unit("100 g").build())
				.build();

		webTestClient.post()
				.uri("/api/entry")
				.body(BodyInserters.fromObject(entry))
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
				.jsonPath("$.createdAt").isEqualTo("2018-12-03T10:12:00")
				.jsonPath("$.food.name").isEqualTo("bar")
				.jsonPath("$.amount").isEqualTo(1)
				.jsonPath("$.id").isNotEmpty();
	}


    @Test
    public void itShouldFailToFindFood() {
        Mockito.when(foodServiceAdapter.byId("1234avc")).thenReturn(Mono.empty());
        Entry entry = Entry.builder()
                .amount(1)
                .createdAt(LocalDateTime.parse("2018-12-03T10:12:00"))
                .owner("tester")
                .food(Food.builder().brand("Great Value").calories(100).carbs(20).fat(3).protein(1).name("bar").id("1234avc").unit("100 g").build())
                .build();

        webTestClient.post()
                .uri("/api/entry")
                .body(BodyInserters.fromObject(entry))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .consumeWith(
                        (content) -> {
                            try {
                                System.out.println(new String(content.getResponseBodyContent(), "UTF-8"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
    }


}

