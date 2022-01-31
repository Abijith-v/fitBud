package com.example.fitnessapp.diets.category;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.example.fitnessapp.diets.Food;
import com.example.fitnessapp.diets.adapters.CategoriesRecyclerViewAdapter;
import com.example.fitnessapp.diets.adapters.RecyclerViewEachCategory;
import com.example.fitnessapp.diets.misc;
import com.example.fitnessapp.diets.recipe.details;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.OnClick;

public class categoryFrag extends Fragment implements categoryInterface {

    RecyclerView recyclerView;
    CircularProgressIndicator progressBar;

    String categoryDescStr, categoryImgUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCategory);
        progressBar = view.findViewById(R.id.progressBarCategoryRV);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            categoryDescStr = getArguments().getString("categoryDescriptionArgs");
            categoryImgUrl = getArguments().getString("categoryPicArgs");

            categorySetter setter = new categorySetter(this);
            //send category name
            setter.getMealByCategory(getArguments().getString("categoryNameArgs"));
        }

    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setMeals(List<Food.Meal> meals) {
        RecyclerViewEachCategory adapter = new RecyclerViewEachCategory(getActivity(), meals);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setClipToPadding(false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener((v, position) -> {

            TextView foodNameTextView = v.findViewById(R.id.mealName);
            String foodName = foodNameTextView.getText().toString();

            Intent intent = new Intent(getActivity(), details.class);

            // pass the name of food
            intent.putExtra("foodNameFromHealthFrag", foodName);
            startActivity(intent);
        });
    }

    @Override
    public void onErrorLoading(String message) {
        misc.showDialogMessage(getActivity(), "Error ", message);
    }

//    @OnClick(R.id.cardCategory)
//    public void onClick() {
//        descDialog.setPositiveButton("CLOSE", (dialog, which) -> dialog.dismiss());
//        descDialog.show();
//    }

}