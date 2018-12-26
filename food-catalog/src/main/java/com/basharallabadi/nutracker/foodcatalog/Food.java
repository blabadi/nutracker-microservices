package com.basharallabadi.nutracker.foodcatalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "foods")
public class Food {

    @Id
    private String id;
    private String name;
    private String brand;
    private String unit;
    private float protein;
    private float fat;
    private int calories;
    private  float carbs;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public int getCalories() {
        return calories;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return Float.compare(food.protein, protein) == 0 &&
                Float.compare(food.fat, fat) == 0 &&
                calories == food.calories &&
                Float.compare(food.carbs, carbs) == 0 &&
                Objects.equals(id, food.id) &&
                Objects.equals(name, food.name) &&
                Objects.equals(brand, food.brand) &&
                Objects.equals(unit, food.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, brand, unit, protein, fat, calories, carbs);
    }

    @Override
    public String toString() {
        return "Food{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", unit='" + unit + '\'' +
                ", protein=" + protein +
                ", fat=" + fat +
                ", calories=" + calories +
                ", carbs=" + carbs +
                '}';
    }


}