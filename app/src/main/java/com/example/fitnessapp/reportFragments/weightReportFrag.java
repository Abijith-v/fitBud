package com.example.fitnessapp.reportFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class weightReportFrag extends Fragment {

    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;

    TextView heading;
    BarChart weightBarChart;
    Legend legend;

    ArrayList<BarEntry> barGraph = new ArrayList<>();
    CircularProgressIndicator pb;

    Context context;

    @Override
    public void onAttach(@NonNull @NotNull Context c) {
        super.onAttach(c);
        context = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weight_report, container, false);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        SharedPreferences themeSP = getActivity().getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);
        int themeKey = themeSP.getInt("nightModeOn", 0);

        heading = view.findViewById(R.id.weightReportHeading);
        pb = view.findViewById(R.id.weightGraphPB);
        weightBarChart = view.findViewById(R.id.weightGraph);
        legend = weightBarChart.getLegend();

        if(themeKey == 2) heading.setTextColor(ContextCompat.getColor(context, R.color.white));

        fireStore.collection("weightReport").document(fireAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(!documentSnapshot.exists()) {

                        Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> map = documentSnapshot.getData();

                    if(map == null) return;

                    ArrayList<String> dates = new ArrayList<>();
                    for(Map.Entry<String, Object> entry : map.entrySet()) {

                        String currKey = entry.getKey();
                        int index = currKey.charAt(currKey.length() - 1) - '0';

                        dates.add(currKey.substring(0, currKey.length() - 1));
                        barGraph.add(new BarEntry(index,  Integer.parseInt((String) entry.getValue())));
                    }

                    Collections.reverse(dates);
                    SetChart(dates, themeKey);
                    pb.setVisibility(View.INVISIBLE);
                    weightBarChart.setVisibility(View.VISIBLE);
                }
            }
        });


        return view;
    }

    private void SetChart(ArrayList<String> dates, int theme) {

        final Typeface font = ResourcesCompat.getFont(context, R.font.urbanist_extrabold);

        weightBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));
        BarDataSet dataSet = new BarDataSet(barGraph, "Weight");
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        dataSet.setColors(ContextCompat.getColor(context, R.color.BGcolor));
        dataSet.setValueTextSize(15);
        dataSet.setValueTypeface(font);

        weightBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        weightBarChart.getXAxis().setTextSize(12);

        if(theme == 2) weightBarChart.getXAxis().setTextColor(ContextCompat.getColor(context, R.color.white));
        else weightBarChart.getXAxis().setTextColor(ContextCompat.getColor(context, R.color.BGcolor));

        weightBarChart.getXAxis().setTypeface(font);
        weightBarChart.setExtraOffsets(10,10,10,10);
        weightBarChart.setElevation(5);

        weightBarChart.getAxisRight().setEnabled(false);
        weightBarChart.getAxisLeft().setEnabled(false);

        BarData barData = new BarData(dataSet);
        barData.setHighlightEnabled(false);
        if(theme == 2) barData.setValueTextColor(ContextCompat.getColor(context, R.color.white));
        else barData.setValueTextColor(ContextCompat.getColor(context, R.color.BGcolor));

        weightBarChart.setData(barData);
        weightBarChart.setDescription(null);
        weightBarChart.animateY(1200);

        weightBarChart.setScaleEnabled(false);

        weightBarChart.setFitBars(true);
        weightBarChart.setFitsSystemWindows(true);
        weightBarChart.getAxisLeft().setDrawGridLines(false);
        weightBarChart.getXAxis().setDrawGridLines(false);
        weightBarChart.getXAxis().setGranularityEnabled(true);
        weightBarChart.getAxisRight().setDrawGridLines(false);

        weightBarChart.getAxisLeft().setDrawAxisLine(false);
        weightBarChart.getXAxis().setDrawAxisLine(false);
        weightBarChart.getAxisRight().setDrawAxisLine(false);;
//
        weightBarChart.invalidate();


        legend.setEnabled(false);
    }
}