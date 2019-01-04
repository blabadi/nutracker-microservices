package com.basharallabadi.nutracker.foodcatalog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
class FoodSvc {
    private FoodRepo foodRepo;
    // we can avoid having double repos here by merging these both according to spring-data rules
    // https://stackoverflow.com/questions/19583540/spring-data-jpa-no-property-found-for-type-exception
    // but i didn't like it.
    @Autowired
    FoodRepoExtended extendedFoodRepo;

    @Autowired
    public FoodSvc(FoodRepo foodRepo, FoodRepoExtended extendedFoodRepo) {
        this.foodRepo = foodRepo;
        this.extendedFoodRepo = extendedFoodRepo;
    }


    Mono<Food> createFood(Food f) {
        return foodRepo.save(f);
    }

    Flux<Food> searchFoodByName(String name) {
        log.info("searchFoodByName called with : {}", name);
        return extendedFoodRepo.searchFoodByName(name);
    }

    Mono<Food> byId(String id) {
        return foodRepo.findById(id).switchIfEmpty(Mono.error(new NotFound()));
    }
}