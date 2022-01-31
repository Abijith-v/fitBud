package com.example.fitnessapp.HomeScreen;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.fitnessapp.BGservice;
import com.example.fitnessapp.MainActivity;
import com.example.fitnessapp.R;
import com.example.fitnessapp.cards.stepsCard;
import com.example.fitnessapp.loginAndSignup.FillDetails;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.SEARCH_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class HomeFrag extends Fragment {

    TextView stepsTextView, caloriesTextView, distanceTextView, stepGoalDisp, slashTV;
    TextView stepsHeading, caloryHeading, distanceHeading, heightHeading, weightHeading, BMI_Heading;
    TextView BMITextView;
    TextView weightToDisplay, heightToDisplay;
    LinearProgressIndicator stepsPB;
    CircularProgressIndicator bmiProgressBar, mainPB;
    LinearLayout mainLayout;

    MaterialCardView stepCard;
    MaterialCardView weightCard, heightCard;

    static int stepCount, weight, height, stepGoal = -1;
    static float distance, calories;
    static int pbVal = 1;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;

    SharedPreferences.OnSharedPreferenceChangeListener listener;

    Context context;

    @Override
    public void onAttach(@NonNull @NotNull Context c) {
        super.onAttach(c);
        context = c;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(firebaseAuth.getCurrentUser() == null) return;

        SharedPreferences sharedPreferences = context.getSharedPreferences("stepCounter", Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        updateData(stepCount, distance);
    }

    private void updateData(int steps, float d) {

        if(FirebaseAuth.getInstance().getCurrentUser() == null) return;
        Log.d("homeFrag", "we reached here");

        mainPB.setVisibility(View.VISIBLE);

        //get weight, height, stepGoal from firestore
        documentReference = firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot documentSnapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {


                if(documentSnapshot == null) return;

                if(!documentSnapshot.exists()) {

//                    Toast.makeText(getActivity(), "Fill details first", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getActivity(), FillDetails.class);
//                    startActivity(intent);

                    return;
                }

                mainLayout.setVisibility(View.VISIBLE);
                mainPB.setVisibility(View.INVISIBLE);

                String weightInStr = documentSnapshot.getString("Weight");

                weightToDisplay.setText(weightInStr + " KG");
                weight = Integer.parseInt(weightInStr);

                String heightInStr = documentSnapshot.getString("Height");
                heightToDisplay.setText(heightInStr + " CM");
                height = Integer.parseInt(heightInStr);

                String stepGoalStr = documentSnapshot.getString("StepGoal");
                stepGoalDisp.setText(stepGoalStr);
                stepGoal = Integer.parseInt(stepGoalStr);

                stepsTextView.setText(String.valueOf(steps));
                int tempVal = (int) ((1.0 * steps / stepGoal) * 100);
                stepsPB.setProgressCompat(tempVal, true);

                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);

                String temp = df.format(((weight * 0.708) * ((steps * 60.0)/100000))) + " KCAL";
                caloriesTextView.setText(temp);

                temp = d + " KM";
                distanceTextView.setText(temp);

                setBMI(weight, 1.0 * height / 100, df);
            }
        });

        mainPB.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = context.getSharedPreferences("stepCounter", Context.MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        SharedPreferences themeSP = getActivity().getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);
        int themeKey = themeSP.getInt("nightModeOn", 0);

        stepsTextView = view.findViewById(R.id.stepCountDisp);
        distanceTextView = view.findViewById(R.id.distanceCovered);
        caloriesTextView = view.findViewById(R.id.caloriesBurned);
        weightToDisplay = view.findViewById(R.id.weightDisplayInCard);
        heightToDisplay = view.findViewById(R.id.heightDisplayInCard);
        stepCard = view.findViewById(R.id.stepsCardView);
        stepGoalDisp = view.findViewById(R.id.stepGoalTextView);
        stepsPB = view.findViewById(R.id.stepsProgress);
        weightCard = view.findViewById(R.id.weightCardView);
        heightCard = view.findViewById(R.id.heightCardView);
        BMITextView = view.findViewById(R.id.BMItext);
        bmiProgressBar = view.findViewById(R.id.BMIpb);
        mainPB = view.findViewById(R.id.homePagePB);
        mainLayout = view.findViewById(R.id.mainLayoutHomeFrag);
        stepsHeading = view.findViewById(R.id.stepsHeadingInCard);
        caloryHeading = view.findViewById(R.id.caloriesInCardHeading);
        distanceHeading = view.findViewById(R.id.distanceHeadingInCard);
        heightHeading = view.findViewById(R.id.heightHeadingInHome);
        weightHeading = view.findViewById(R.id.weightHeadingInHome);
        BMI_Heading = view.findViewById(R.id.BMIheadingInCard);
        slashTV = view.findViewById(R.id.slashInHome);

        mainLayout.setVisibility(View.INVISIBLE);
        if(themeKey == 2) {
            ColorStateList white = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white));

            stepsHeading.setTextColor(white);
            caloryHeading.setTextColor(white);
            distanceHeading.setTextColor(white);
            heightHeading.setTextColor(white);
            weightHeading.setTextColor(white);
            BMI_Heading.setTextColor(white);
            slashTV.setTextColor(white);
        }


        final SharedPreferences sharedPreferences = context
                                                   .getSharedPreferences("stepCounter", Context.MODE_PRIVATE);

        Log.d("steps in home :", "" + sharedPreferences.getInt("steps", 0));

        stepCount = sharedPreferences.getInt("steps",0);
        distance = sharedPreferences.getFloat("distance", 0);

        updateData(stepCount, distance);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if(key.equals("steps")) {

                    stepCount = prefs.getInt("steps",0);
                    stepsTextView.setText(String.valueOf(stepCount));

                    stepsPB.setProgressCompat((int) ((1.0*stepCount/stepGoal) * 100), true);

                    distance = prefs.getFloat("distance",0);

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    String temp = df.format(((weight * 0.708) * ((stepCount * 60.0)/100000))) + " KCAL";
                    caloriesTextView.setText(temp);
                    distanceTextView.setText(distance + " KM");
                }
            }
        };

        //cards onClicks

        //steps card
        stepCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, stepsCard.class);
                intent.putExtra("StepsGoalFromHomeFrag", stepGoal);

                SharedPreferences sharedPreferences = context.getSharedPreferences("stepCounter", Context.MODE_PRIVATE);
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);

                startActivity(intent);
            }
        });

        //weight card
        weightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //launch dialog box to input new weight

                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                View mview = getLayoutInflater().inflate(R.layout.edit_weight_card_dialog,null);

                RelativeLayout layout = mview.findViewById(R.id.weightDialogLayout);

                if(themeKey == 2)
                    layout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.mildBlack)));
                else
                    layout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));

                TextInputEditText newWeightEditText = mview.findViewById(R.id.WeightTVinDialog);
                TextInputLayout TILforWeightGoal = mview.findViewById(R.id.TILforWeightDialog);
                MaterialButton confirmBtn = mview.findViewById(R.id.confirmBtninUpdateWeight);
                MaterialButton cancelBtn = mview.findViewById(R.id.cancelBtninUpdateWeight);
                CircularProgressIndicator circularPB = mview.findViewById(R.id.confirmBtnPBinUpdateWeight);

//                DocumentReference documentReference = firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());

                newWeightEditText.setHint("Enter new weight in KGs");
//                newWeightEditText.setHintTextColor(Color.);

                alert.setView(mview);
                final AlertDialog alertDialog = alert.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);

                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String newWeight = newWeightEditText.getText().toString();

                        if(newWeight.isEmpty() ) {
                            TILforWeightGoal.setError("Please enter new weight");
                            return;
                        }

                        TILforWeightGoal.setError(null);
                        circularPB.setVisibility(View.VISIBLE);
                        confirmBtn.setVisibility(View.INVISIBLE);


                        documentReference.update("Weight", newWeight).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                                circularPB.setVisibility(View.INVISIBLE);
                                confirmBtn.setVisibility(View.VISIBLE);

                                if(task.isSuccessful()) {
                                    //display tick animation

                                    putInWeightReport(newWeight);
                                    alertDialog.dismiss();
                                    Toast.makeText(context, "Weight updated", Toast.LENGTH_SHORT).show();
                                    weightToDisplay.setText(newWeight +  " KG");
                                }
                                else
                                    Toast.makeText(context, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });

        //height card
        heightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //launch dialog box to input new height
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                View mview = getLayoutInflater().inflate(R.layout.edit_weight_card_dialog,null);

                TextInputEditText newHeightEditText = mview.findViewById(R.id.WeightTVinDialog);
                TextInputLayout TILforHeightGoal = mview.findViewById(R.id.TILforWeightDialog);
                TextView dialogTitle = mview.findViewById(R.id.WeightDialogHeading);
                MaterialButton confirmBtn = mview.findViewById(R.id.confirmBtninUpdateWeight);
                MaterialButton cancelBtn = mview.findViewById(R.id.cancelBtninUpdateWeight);
                CircularProgressIndicator circularPB = mview.findViewById(R.id.confirmBtnPBinUpdateWeight);
                RelativeLayout layout = mview.findViewById(R.id.weightDialogLayout);

                if(themeSP.getInt("nightModeOn", 0) == 2)
                    layout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.mildBlack)));
                else
                    layout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));

                dialogTitle.setText("Set new height");
                newHeightEditText.setHint("Enter new height in CMs");

                alert.setView(mview);
                final AlertDialog alertDialog = alert.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);

                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String newHeight = newHeightEditText.getText().toString();

                        if(newHeight.isEmpty() ) {
                            TILforHeightGoal.setError("Please enter new height");
                            return;
                        }

                        TILforHeightGoal.setError(null);
                        circularPB.setVisibility(View.VISIBLE);
                        confirmBtn.setVisibility(View.INVISIBLE);

                        documentReference.update("Height", newHeight).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                                if(task.isSuccessful()) {
                                    //display tick animation
                                    alertDialog.dismiss();
                                    heightToDisplay.setText(newHeight + " CM");
                                    Toast.makeText(context, "Height updated", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(context, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });

        return view;
    }

    private void putInWeightReport(String newWeight) {

        DocumentReference weightReportRef = firestore.collection("weightReport").document(firebaseAuth.getCurrentUser().getUid());
        weightReportRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {

//                    Toast.makeText(context, "Control reached here", Toast.LENGTH_SHORT).show();

                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(!documentSnapshot.exists()) {
                        Log.d("document", "doesnt exist");
                        return;
                    }

                    Map<String, Object> map = documentSnapshot.getData();

                    Log.d("Map size :", String.valueOf(map.size()));

                    //21/021 -> 21 = day, 02 = month, 1 = index
                    String todaysDate = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date());
                    if(map.size() < 7) {

                        Log.d("new weight added", "Success");

                        map.put(todaysDate + String.valueOf(map.size()), newWeight);
                        weightReportRef.set(map, SetOptions.merge());
                        return;
                    }

                    Map<String, Object> newMap = new HashMap<>();
                    for(Map.Entry<String, Object> entry : map.entrySet()) {

                        String date = entry.getKey();
                        char index = date.charAt(date.length() - 1);

                        if(index == '0') continue;

                        StringBuilder sb = new StringBuilder(date);
                        sb.deleteCharAt(date.length() - 1);
                        newMap.put(date.substring(0, date.length() - 1) + ((index - '0') - 1), entry.getValue());
                    }

                    newMap.put(todaysDate + "6", newWeight);
                    weightReportRef.set(newMap);
                }
                else {
                    Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setBMI(int weight, double height, DecimalFormat df) {

        if(height <= 0) {
            BMITextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            BMITextView.setText("Invalid Height / Weight");
            return;
        }

        double bmi = weight / (height * height);
        String bmiStr = String.valueOf(df.format(bmi));

        if(bmi < 18.5) {
            BMITextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            BMITextView.setText(bmiStr + " - Underweight");
        }
        else if(bmi < 24.9) {
            BMITextView.setTextColor(ContextCompat.getColor(context, R.color.BGcolor));
            BMITextView.setText(bmiStr + " - Healthy");
        }
        else if(bmi < 29.9) {
            BMITextView.setTextColor(ContextCompat.getColor(context, R.color.mildYellow));
            BMITextView.setText(bmiStr + " - Overweight");
        }
        else {
            BMITextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            BMITextView.setText(bmiStr + " - Obese");
        }

        bmiProgressBar.setVisibility(View.GONE);
    }
}