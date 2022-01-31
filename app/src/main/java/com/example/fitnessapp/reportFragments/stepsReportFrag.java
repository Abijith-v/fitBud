package com.example.fitnessapp.reportFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fitnessapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class stepsReportFrag extends Fragment {

    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;

    BarChart barChart;
    Legend legend;

    ArrayList<BarEntry> barGraph;
    ArrayList<String> days;

    Context context;

    CircularProgressIndicator progressIndicator;
    TextView heading, errorMsg;

    int backupTodaysNum = 0;

    @Override
    public void onAttach(@NonNull @NotNull Context c) {
        super.onAttach(c);
        context = c;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_steps_report, container, false);

        SharedPreferences themeSP = getActivity().getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);
        int themeKey = themeSP.getInt("nightModeOn", 0);

        progressIndicator = view.findViewById(R.id.stepGraphPB);
        barChart = view.findViewById(R.id.progressGraph);
        legend = barChart.getLegend();
        heading = view.findViewById(R.id.stepReportHeading);
        errorMsg = view.findViewById(R.id.stepsBarGraphError);

        if (themeKey == 2) heading.setTextColor(ContextCompat.getColor(context, R.color.white));

        String[] daysInOrder = new String[7];
        daysInOrder[0] = "Sun";
        daysInOrder[1] = "Mon";
        daysInOrder[2] = "Tue";
        daysInOrder[3] = "Wed";
        daysInOrder[4] = "Thu";
        daysInOrder[5] = "Fri";
        daysInOrder[6] = "Sat";

        barGraph = new ArrayList<>();
        days = new ArrayList<>();

        LocalDate localDate = LocalDate.now();
        int todaysDayNum = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
//
        Log.d("day num", "" + todaysDayNum);

//        daysInOrder[todaysDayNum - 1] = "Today";

        backupTodaysNum = --todaysDayNum;
        for (int i = 0; i < 6; i++) {
            todaysDayNum = (todaysDayNum + 1) % 7;
            days.add(daysInOrder[todaysDayNum]);
        }
        days.add("Today");

        for (String s : days)
            Log.d("day ", s);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        if (fireAuth.getCurrentUser() == null) return view;

        DocumentReference documentReference = fireStore.collection("weeklySteps").document(fireAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot documentSnapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                if (fireAuth.getCurrentUser() == null) return;

                SharedPreferences sharedPreferences = context.getSharedPreferences("stepCounter", Context.MODE_PRIVATE);

                int currSteps = sharedPreferences.getInt("steps", 0);
                barGraph.add(new BarEntry(6, currSteps));


                Log.d("today :", String.valueOf(backupTodaysNum));

                boolean noSteps = currSteps == 0;
                for (int i = 5; i >= 0; i--) {

                    backupTodaysNum = (backupTodaysNum - 1 + 7) % 7;

                    String key = String.valueOf(backupTodaysNum);

                    if (documentSnapshot.get(key) != null) {
                        barGraph.add(new BarEntry(i, (long) documentSnapshot.get(key)));
                        noSteps = false;
                    }
                    else
                        barGraph.add(new BarEntry(i, (long) 0));

                }

                progressIndicator.setVisibility(View.INVISIBLE);

                if(noSteps == true) {

                    errorMsg.setVisibility(View.VISIBLE);
                    return;
                }

                //Set charts
                SetChart(themeKey);
                barChart.setVisibility(View.VISIBLE);
            }
        });


        return view;
    }


    private void SetChart(int theme) {

        final Typeface font = ResourcesCompat.getFont(context, R.font.urbanist_extrabold);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));
        ;
        BarDataSet dataSet = new BarDataSet(barGraph, "STEPS");
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        dataSet.setColors(ContextCompat.getColor(context, R.color.BGcolor));
        dataSet.setValueTextSize(15);
        dataSet.setValueTypeface(font);

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextSize(15);
//        barChart.getAxis().setTextColor(ContextCompat.getColor(context, R.color.BGcolor));

        if(theme == 2) barChart.getXAxis().setTextColor(ContextCompat.getColor(context, R.color.white));
        else barChart.getXAxis().setTextColor(ContextCompat.getColor(context, R.color.BGcolor));


        barChart.getXAxis().setTypeface(font);
        barChart.setExtraOffsets(5, 5, 5, 10);
        barChart.setElevation(5);

        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);

        BarData barData = new BarData(dataSet);
        barData.setHighlightEnabled(false);

        if(theme == 2) barData.setValueTextColor(ContextCompat.getColor(context, R.color.white));
        else barData.setValueTextColor(ContextCompat.getColor(context, R.color.BGcolor));

        barChart.setData(barData);
        barChart.setDescription(null);
        barChart.animateY(1000);

        barChart.setScaleEnabled(false);

        barChart.setFitBars(true);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.getAxisRight().setDrawAxisLine(false);
        ;

        barChart.invalidate();

        legend.setEnabled(false);
    }
}