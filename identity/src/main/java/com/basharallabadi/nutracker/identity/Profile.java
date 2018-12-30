package com.basharallabadi.nutracker.identity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Profile {
    private String firstName;
    private String lastName;
    private int heightCm;
    private float currentWeight;
    private float targetWeight;
    private float fatPercentage;
    private NutritionalDailyTargets targets;
}

@Builder
@Getter
class NutritionalDailyTargets {
    private int calories;
    private int fats;
    private int carbs;
    private int protein;
}