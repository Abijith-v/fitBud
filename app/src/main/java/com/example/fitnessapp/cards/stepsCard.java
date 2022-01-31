package com.example.fitnessapp.cards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.BGservice;
import com.example.fitnessapp.HomeScreen.HomeFrag;
import com.example.fitnessapp.MainActivity;
import com.example.fitnessapp.R;
import com.example.fitnessapp.workoutPage.MapsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import me.itangqi.waveloadingview.WaveLoadingView;

public class stepsCard extends AppCompatActivity {

    TextView stepCountInsidePB, stepGoalTextView, stepGoalInsidePB;
    WaveLoadingView progressIndicator;
    LinearLayout updateStepGoal;

    int Steps, StepGoalFromIntent; // get from firestore.. to be implemented

    SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener listenerInStepsCard;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    MaterialCardView resetButton, backButton;
    
    boolean pauseButtonON = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_card);

        //register listener
        sharedPreferences = this.getSharedPreferences("stepCounter", Context.MODE_PRIVATE);

        stepCountInsidePB = findViewById(R.id.stepCountInPB);
        stepGoalTextView = findViewById(R.id.stepGoalInStepCard);
        stepGoalInsidePB = findViewById(R.id.stepGoalInPB);
        progressIndicator = findViewById(R.id.waveProgressbar);
        backButton = findViewById(R.id.backButtonSteps);
        updateStepGoal = findViewById(R.id.stepGoalUpdate);
        resetButton = findViewById(R.id.resetBtn);
        
        //Get stepGoal from firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        progressIndicator.setProgressValue(0);
        progressIndicator.setAnimDuration(2000);
        progressIndicator.startAnimation();

        //step goal from intent
        Intent intent = getIntent();
        StepGoalFromIntent = intent.getExtras().getInt("StepsGoalFromHomeFrag");
        stepGoalTextView.setText(String.valueOf(StepGoalFromIntent));
        stepGoalInsidePB.setText(String.valueOf(StepGoalFromIntent));

        Steps = sharedPreferences.getInt("steps",0);
        stepCountInsidePB.setText(String.valueOf(Steps));
        progressIndicator.setProgressValue((int) ((1.0 * Steps / StepGoalFromIntent) * 100));

        listenerInStepsCard = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                if(key.equals("steps")) {
                    Steps = prefs.getInt("steps", 0);
                    stepCountInsidePB.setText(String.valueOf(Steps));
                    progressIndicator.setProgressValue((int) ((1.0 * Steps / StepGoalFromIntent) * 100));
                }
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(listenerInStepsCard);

        //update step goal with dialog box
        updateStepGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //launch dialog to ask user to input new step goal

                final AlertDialog.Builder alert = new AlertDialog.Builder(stepsCard.this);
                View mview = getLayoutInflater().inflate(R.layout.activity_edit_step_goal_dialog,null);


                TextInputEditText newStepGoalEditText = mview.findViewById(R.id.stepGoalFromEditText);
                TextInputLayout TILforSpteGoal = mview.findViewById(R.id.textInputLayoutinStepsDialog);
                MaterialButton confirmBtn = mview.findViewById(R.id.confirmBtninUpdateStepGoal);
                MaterialButton cancelBtn = mview.findViewById(R.id.cancelBtninUpdateStepGoal);
                CircularProgressIndicator circularPB = mview.findViewById(R.id.confirmBtnPBinUpdateStepGoal);

                DocumentReference documentReference = firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());

                alert.setView(mview);
                final AlertDialog alertDialog = alert.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);

                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String newStepGoalStr = newStepGoalEditText.getText().toString();

                        if(newStepGoalStr.isEmpty() ) {
                            TILforSpteGoal.setError("Please enter a new step goal");
                            return;
                        }

                        TILforSpteGoal.setError(null);
                        circularPB.setVisibility(View.VISIBLE);
                        confirmBtn.setVisibility(View.INVISIBLE);

                        documentReference.update("StepGoal", newStepGoalStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                                circularPB.setVisibility(View.INVISIBLE);
                                confirmBtn.setVisibility(View.VISIBLE);

                                if(task.isSuccessful()) {
                                    //display tick animation
                                    alertDialog.dismiss();
                                    stepGoalTextView.setText(newStepGoalStr);
                                    stepGoalInsidePB.setText(newStepGoalStr);

                                    progressIndicator.setProgressValue((int) ((1.0 * Steps / Integer.parseInt(newStepGoalStr)) * 100));
                                    Toast.makeText(stepsCard.this, "New step goal set!!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(stepsCard.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listenerInStepsCard);
                finish();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(stepsCard.this));
                View mview = getLayoutInflater().inflate(R.layout.info_dialog_layout, null);

                MaterialCardView bg = mview.findViewById(R.id.mapsDialogBG);
                MaterialTextView mainHeadingOfDialog = mview.findViewById(R.id.saveDialogMainHeading);
                MaterialTextView sideHeadingOfDialog = mview.findViewById(R.id.saveDialogSideHeading);
                MaterialCardView okBtn = mview.findViewById(R.id.saveButtonDialog);
                MaterialCardView cancelBtn = mview.findViewById(R.id.cancelBtnDialog);

                alert.setView(mview);
                AlertDialog alertDialog = alert.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                mainHeadingOfDialog.setText("Reset todays steps?");
                sideHeadingOfDialog.setVisibility(View.GONE);

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        sharedPreferences.edit().putInt("steps", 0).apply();
                        sharedPreferences.edit().putFloat("distance", 0).apply();
                        sharedPreferences.edit().putFloat("calories", 0).apply();

                        Log.d("steps after reset :", "" + sharedPreferences.getInt("steps", 0));
                        progressIndicator.setProgressValue(0);
                        stepCountInsidePB.setText("0");
                        alertDialog.dismiss();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listenerInStepsCard);
        finish();
    }
}