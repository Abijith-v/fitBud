package com.example.fitnessapp.diets.category;

import com.example.fitnessapp.diets.Food;

import java.util.List;

public interface categoryInterface {
    void showLoading();
    void hideLoading();
    void setMeals(List<Food.Meal> meals);
    void onErrorLoading(String message);
}
