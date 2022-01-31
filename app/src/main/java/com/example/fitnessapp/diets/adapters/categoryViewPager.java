package com.example.fitnessapp.diets.adapters;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.example.fitnessapp.diets.Categories;
import com.example.fitnessapp.diets.category.categoryFrag;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class categoryViewPager extends FragmentPagerAdapter {

    List<Categories.Category> categories;
    public categoryViewPager(FragmentManager fragmentManager, List<Categories.Category> cat) {
        super(fragmentManager);
        this.categories = cat;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int pos) {

        categoryFrag fragment = new categoryFrag();

        Bundle args = new Bundle();
        args.putString("categoryNameArgs", categories.get(pos).getStrCategory());
        args.putString("categoryDescriptionArgs", categories.get(pos).getStrCategoryDescription());
        args.putString("categoryPicArgs", categories.get(pos).getStrCategoryThumb());

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return categories.get(position).getStrCategory();
    }
}
