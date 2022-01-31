package com.example.fitnessapp.diets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.example.fitnessapp.diets.adapters.CategoriesRecyclerViewAdapter;
import com.example.fitnessapp.diets.adapters.featuredViewPager;
import com.example.fitnessapp.diets.category.categoryHomePage;
import com.example.fitnessapp.diets.launcher.home;
import com.example.fitnessapp.diets.launcher.homeSetter;
import com.example.fitnessapp.diets.recipe.details;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;


public class recipeHomePage extends AppCompatActivity implements home {

    ViewPager viewPagerFeatured;
    RecyclerView categoriesRecyclerView;

    homeSetter setter;
    View headShimmer,categoriesShimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_health);

        headShimmer = findViewById(R.id.featuredShimmer);
        categoriesShimmer = findViewById(R.id.categoriesShimmer);

        viewPagerFeatured = findViewById(R.id.viewPagerForFeatured);
        categoriesRecyclerView = findViewById(R.id.recyclerViewForCategories);

        ButterKnife.bind(this);

        setter = new homeSetter(this);
        setter.getFood();
        setter.getCategories();
    }

    @Override
    public void startLoading() {

        headShimmer.setVisibility(View.VISIBLE);
        categoriesShimmer.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        headShimmer.setVisibility(View.GONE);
        categoriesShimmer.setVisibility(View.GONE);
    }

    @Override
    public void setFood(List<Food.Meal> food) {

        featuredViewPager adapter = new featuredViewPager(food, this);
        viewPagerFeatured.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener((v, position) -> {

            TextView foodNameTextView = v.findViewById(R.id.NameOfFood);
            String foodName = foodNameTextView.getText().toString();

            Intent intent = new Intent(this, details.class);

            // pass the name of food
            intent.putExtra("foodNameFromHealthFrag", foodName);
            startActivity(intent);
        });
    }

    @Override
    public void setCategories(List<Categories.Category> categories) {

        //Pass list and context to adapter
        CategoriesRecyclerViewAdapter recyclerViewAdapter = new CategoriesRecyclerViewAdapter(categories, this);
        categoriesRecyclerView.setAdapter(recyclerViewAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        categoriesRecyclerView.setLayoutManager(gridLayoutManager);
        categoriesRecyclerView.setNestedScrollingEnabled(true);

        recyclerViewAdapter.notifyDataSetChanged();

        recyclerViewAdapter.setOnItemClickListener((view, position) -> {

//            Toast.makeText(), "clicked " + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, categoryHomePage.class);
            intent.putExtra("categoryFromHealthFrag", (Serializable) categories);
            intent.putExtra("positionFromHealthFrag", position);
            startActivity(intent);
        });
    }

    @Override
    public void ifException(String msg) {
        Toast.makeText(this, "Failed to connect", Toast.LENGTH_SHORT).show();
    }
}