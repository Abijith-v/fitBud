package com.example.fitnessapp.HomeScreen;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fitnessapp.R;
import com.example.fitnessapp.workoutPage.walkingSession;
import com.example.fitnessapp.workoutPage.walksFrag;
import com.example.fitnessapp.workoutPage.workoutAdapter;
import com.google.android.material.tabs.TabLayout;

public class healthFrag extends Fragment {

    ViewPager viewPagerWorkout;
    TabLayout tableLayout;
    workoutAdapter adapter;

    int[] tabIcons = {R.drawable.walk_image, R.drawable.history_icon};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.workout_fragment, container, false);

        tableLayout = view.findViewById(R.id.workoutTabLayout);
        viewPagerWorkout = view.findViewById(R.id.workoutViewPager);
        adapter = new workoutAdapter(getChildFragmentManager());

        adapter.AddFragment(new walksFrag(),"");
        adapter.AddFragment(new walkingSession(), "");

        viewPagerWorkout.setAdapter(adapter);
        tableLayout.setupWithViewPager(viewPagerWorkout);

        tableLayout.getTabAt(0).setIcon(tabIcons[0]);
        tableLayout.getTabAt(1).setIcon(tabIcons[1]);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}