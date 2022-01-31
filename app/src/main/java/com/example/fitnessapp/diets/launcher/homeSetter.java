package com.example.fitnessapp.diets.launcher;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fitnessapp.diets.Categories;
import com.example.fitnessapp.diets.Food;
import com.example.fitnessapp.diets.misc;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class homeSetter {

    private home view;

    // Constructor
    public homeSetter(home view) {
        this.view = view;
    }

    public void getFood() {

        //call start loading in home interface
        view.startLoading();

        Call<Food> mealsCall = misc.getApi().getFood();
        mealsCall.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(@NonNull Call<Food> call, @NonNull Response<Food> response) {

                //if Success, set random, else show exception

                if (response.isSuccessful() && response.body() != null) {
                    view.stopLoading();
                    view.setFood(response.body().getFood());
                }
                else
                    view.ifException(response.message());
            }
            @Override
            public void onFailure(@NonNull Call<Food> call, @NonNull Throwable throwable) {
//                view.stopLoading();
                view.ifException(throwable.getLocalizedMessage());
            }
        });
    }


    public void getCategories() {

        view.startLoading();

        Call<Categories> categoriesCall = misc.getApi().getCategories();
        categoriesCall.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(@NonNull Call<Categories> call, @NonNull Response<Categories> response) {

                //if Success, set categories, else show exception
                if (response.isSuccessful() && response.body() != null) {
                    view.stopLoading();
                    view.setCategories(response.body().getCategories());
                }
                else
                    view.ifException(response.message());
            }

            @Override
            public void onFailure(@NonNull Call<Categories> call, @NonNull Throwable throwable) {
//                view.stopLoading();
                view.ifException(throwable.getLocalizedMessage());
            }
        });
    }
}
