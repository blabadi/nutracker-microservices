package com.basharallabadi.nutracker.foodcatalog.repo.elastic;

import com.basharallabadi.nutracker.foodcatalog.Food;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodElasticSearchRepo extends ReactiveElasticsearchRepository<Food, String> { }