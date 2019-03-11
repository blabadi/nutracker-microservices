package com.basharallabadi.nutracker.foodcatalog;

import lombok.val;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.regex.Pattern;

@Repository
class CustomFoodRepo {
    @Autowired
    private ReactiveMongoOperations operations;

    @Autowired
    private ReactiveElasticsearchOperations elasticsearchOperations;

    Flux<Food> searchFoodByNameInDb(String name) {
        val findQuery = new Query();
        findQuery.limit(10);
        val p = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        findQuery.addCriteria(Criteria.where("name").regex(p));
        return operations.find(findQuery, Food.class);
    }

    Flux<Food> findFood(String query) {
        return elasticsearchOperations.find(
                new StringQuery(QueryBuilders.queryStringQuery(query + "*").toString()
            ), Food.class);
    }
}
