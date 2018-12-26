package com.basharallabadi.nutracker.foodcatalog;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FoodRepo extends ReactiveCrudRepository<Food, Long> {

}
