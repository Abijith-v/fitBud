package com.example.fitnessapp.diets.recipe;

import com.example.fitnessapp.diets.Food;
import com.example.fitnessapp.diets.misc;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class recipeSetter {

    public recipeInterface rInterface;
    public recipeSetter(recipeInterface Interface) {
        this.rInterface = Interface;
    }

    //api method by id
    void getMealById(String foodName) {

        rInterface.showLoading();
        misc.getApi().getMealByName(foodName).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {

                rInterface.hideLoading();

                if(response.isSuccessful() && response.body() != null)
                    rInterface.setMeals(response.body().getFood().get(0));
                else
                    rInterface.ifExceptions(response.message());
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {

//                rInterface.hideLoading();
                rInterface.ifExceptions(t.getLocalizedMessage());
            }
        });

    }

}
