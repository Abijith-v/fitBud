package com.example.fitnessapp.diets.API;

import com.example.fitnessapp.diets.Categories;
import com.example.fitnessapp.diets.Food;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface foodAPI {

    //Get a random recipe
    @GET("random.php")
    Call<Food> getFood();

    @GET("Categories.php")
    Call<Categories> getCategories();

    @GET("filter.php")
    Call<Food> getMealByCategory(@Query("c") String category);

    @GET("search.php")
    Call<Food> getMealByName(@Query("s") String foodName);
    //Search
//    @GET("search.php")
//    Call<SearchFood> getSearchFood();

}
