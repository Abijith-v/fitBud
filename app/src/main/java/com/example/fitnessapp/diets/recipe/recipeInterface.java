package com.example.fitnessapp.diets.recipe;

import com.example.fitnessapp.diets.Food;

import java.util.List;

public interface recipeInterface {
    
    void showLoading();
    void hideLoading();
    void setMeals(Food.Meal meals);
    void ifExceptions(String msg);
}
