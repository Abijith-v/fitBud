package com.example.fitnessapp.workoutPage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class workoutAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragsAr = new ArrayList<>();
    ArrayList<String> stringsAr = new ArrayList<>();

    public void AddFragment(Fragment f, String s) {

        fragsAr.add(f);
        stringsAr.add(s);
    }

    public workoutAdapter(@NonNull @NotNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {

        return fragsAr.get(position);
    }

    @Override
    public int getCount() {
        return fragsAr.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return stringsAr.get(position);
    }
}
