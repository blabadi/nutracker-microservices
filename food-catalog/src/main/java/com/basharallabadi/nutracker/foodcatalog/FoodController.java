package com.basharallabadi.nutracker.foodcatalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    private FoodSvc foodSvc;

    @Autowired
    public FoodController(FoodSvc foodSvc) {
        this.foodSvc = foodSvc;
    }

    @PostMapping
    public Mono<Food> create(@RequestBody Food food){
        return foodSvc.createFood(food);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Flux<Food> search(@RequestParam("name") String name){
        return foodSvc.searchFoodByName(name);
    }
}