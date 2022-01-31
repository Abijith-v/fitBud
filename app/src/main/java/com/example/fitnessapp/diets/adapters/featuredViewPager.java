package com.example.fitnessapp.diets.adapters;


import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import com.example.fitnessapp.R;
import com.example.fitnessapp.diets.Food;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class featuredViewPager extends PagerAdapter {

    private List<Food.Meal> meals;
    private Context context;
    private static ClickListener clickListener;
    private CircularProgressIndicator imagePB;

    // set the passed meals list to our local meals list with this constructor, do the same for context
    public featuredViewPager(List<Food.Meal> meals, Context context) {
        this.meals = meals;
        this.context = context;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        featuredViewPager.clickListener = clickListener;
    }

    @Override
    public int getCount() {
        return meals.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.featured_viewpager_layout, container, false);

        ImageView foodPic = view.findViewById(R.id.FoodPic);
        TextView foodNameTextView = view.findViewById(R.id.NameOfFood);
        imagePB = view.findViewById(R.id.imageLoadingPB);

        // getStrMealThumb() and getStrMeal() methods are from Food.java from API
        String imgURL = meals.get(position).getStrMealThumb();
        Picasso.get().load(imgURL).into(foodPic, new Callback() {
            @Override
            public void onSuccess() {
                imagePB.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }); // set image to element clicked at position

        String foodName= meals.get(position).getStrMeal(); // get food name from api and put it into textview

        if(foodName.length() > 15) foodNameTextView.setTextSize(13);
        else if(foodName.length() > 20) foodNameTextView.setTextSize(9);
        else if(foodName.length() > 30) foodNameTextView.setTextSize(5);
        foodNameTextView.setText(foodName);

        view.setOnClickListener(v -> clickListener.onClick(v, position));

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    public interface ClickListener {
        void onClick(View v, int position);
    }
}

