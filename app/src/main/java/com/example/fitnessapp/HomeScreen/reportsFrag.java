package com.example.fitnessapp.HomeScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.motion.utils.Easing;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fitnessapp.R;
import com.example.fitnessapp.reportFragments.stepsReportFrag;
import com.example.fitnessapp.reportFragments.weightReportFrag;
import com.example.fitnessapp.workoutPage.walkingSession;
import com.example.fitnessapp.workoutPage.walksFrag;
import com.example.fitnessapp.workoutPage.workoutAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class reportsFrag extends Fragment {

    ViewPager viewPagerReports;
    TabLayout tableLayoutReports;
    workoutAdapter reportsAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        int[] tabIcons = {R.drawable.walk_image, R.drawable.weight_icon};

        tableLayoutReports = view.findViewById(R.id.reportsTabLayout);
        viewPagerReports = view.findViewById(R.id.reportsViewPager);
        reportsAdapter = new workoutAdapter(getChildFragmentManager());

        reportsAdapter.AddFragment(new stepsReportFrag(),"");
        reportsAdapter.AddFragment(new weightReportFrag(), "");

        viewPagerReports.setAdapter(reportsAdapter);
        tableLayoutReports.setupWithViewPager(viewPagerReports);

        tableLayoutReports.getTabAt(0).setIcon(tabIcons[0]);
        tableLayoutReports.getTabAt(1).setIcon(tabIcons[1]);

        return view;
    }

}