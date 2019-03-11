package com.basharallabadi.nutracker.foodcatalog;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Document(collection = "foods")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "food")
public class Food {
    @Id
    private String id;
    private String name;
    private String brand;
    private String unit;
    private float protein;
    private float fat;
    private int calories;
    private float carbs;
}