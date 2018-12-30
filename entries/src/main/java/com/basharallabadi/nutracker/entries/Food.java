package com.basharallabadi.nutracker.entries;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    private String id;
    private String name;
    private String brand;
    private String unit;
    private float protein;
    private float fat;
    private int calories;
    private float carbs;
}
