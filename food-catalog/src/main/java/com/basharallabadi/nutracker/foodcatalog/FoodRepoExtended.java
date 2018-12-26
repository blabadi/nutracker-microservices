package com.basharallabadi.nutracker.foodcatalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.regex.Pattern;

@Repository
class FoodRepoExtended {
    @Autowired
    private ReactiveMongoOperations operations;

    Flux<Food> searchFoodByName(String name) {
        Query findQuery = new Query();
        findQuery.limit(10);
        Pattern p = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        findQuery.addCriteria(Criteria.where("name").regex(p));
        return operations.find(findQuery, Food.class);
    }
}
