package com.basharallabadi.nutracker.foodcatalog;

import com.basharallabadi.nutracker.foodcatalog.repo.elastic.FoodElasticSearchRepo;
import com.basharallabadi.nutracker.foodcatalog.repo.mongo.FoodDbRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
class FoodSvc {
    private FoodDbRepo foodDbRepo;

    private CustomFoodRepo extendedFoodRepo;

    private FoodElasticSearchRepo elasticSearchRepo;

    @Autowired
    public FoodSvc(FoodDbRepo foodDbRepo, CustomFoodRepo extendedFoodRepo, FoodElasticSearchRepo elasticSearchRepo) {
        this.foodDbRepo = foodDbRepo;
        this.extendedFoodRepo = extendedFoodRepo;
        this.elasticSearchRepo = elasticSearchRepo;
    }

    Mono<Food> createFood(Food f) {
        return foodDbRepo.save(f)
            .flatMap(
                food -> elasticSearchRepo.save(food)
            );

    }

    Flux<Food> searchFoodByName(String name) {
        log.info("searchFoodByName called with : {}", name);
        return extendedFoodRepo.findFood(name)
                .onErrorMap((e) -> {
                    e.printStackTrace();
                    return new RuntimeException("error finding food", e);
                });
    }

    Mono<Food> byId(String id) {
        return foodDbRepo.findById(id)
            .switchIfEmpty(Mono.error(new NotFound()));
    }
}