package com.basharallabadi.nutracker.foodcatalog.repo.mongo;

import com.basharallabadi.nutracker.foodcatalog.Food;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodDbRepo extends ReactiveCrudRepository<Food, String>{ }