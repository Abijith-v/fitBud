package com.example.fitnessapp.diets.launcher;

import com.example.fitnessapp.diets.Categories;
import com.example.fitnessapp.diets.Food;
import java.util.List;

public interface home {

    void startLoading();
    void stopLoading();
    void setFood(List<Food.Meal> meals);
    void setCategories(List<Categories.Category> categories);
    void ifException(String msg);
}
