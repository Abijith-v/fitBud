package com.example.fitnessapp.diets.category;

import androidx.annotation.NonNull;

import com.example.fitnessapp.diets.Food;
import com.example.fitnessapp.diets.misc;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class categorySetter {

    categoryInterface view;
    public categorySetter(categoryInterface view) {
        this.view = view;
    }

    void getMealByCategory(String category) {

        //get category name and send it to API
        view.showLoading();
        misc.getApi().getMealByCategory(category).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(@NonNull Call<Food> call, @NonNull Response<Food> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null)
                    view.setMeals(response.body().getFood());
                else
                    view.onErrorLoading(response.message());
            }

            @Override
            public void onFailure(@NonNull Call<Food> call,@NonNull Throwable t) {
//                view.hideLoading();
                view.onErrorLoading(t.getLocalizedMessage());
            }
        });

    }

}
