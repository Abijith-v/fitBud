package com.example.fitnessapp.loginAndSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fitnessapp.MainActivity;
import com.example.fitnessapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FillDetails extends AppCompatActivity {

    TextInputEditText usernameFromInput, ageFromInput, weightFromInput, heightFromInput;
    TextInputLayout usernameTIL, ageTIL, weightTIL, heightTIL;
    MaterialButton createAccButton, resendBtn;
    CircularProgressIndicator progressBar;
    MaterialTextView errorMsg;
    FloatingActionButton reloadFab;

    //FireStore and fire auth
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onResume() {
        super.onResume();

        if (user == null) return;

        Log.d("FillDetails", "resumed");
        reloadPage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_details);

        //UID and email
        firebaseAuth = FirebaseAuth.getInstance();
        String CurrUserID = firebaseAuth.getCurrentUser().getUid();
        String currUserEmail = firebaseAuth.getCurrentUser().getEmail();

        firestore = FirebaseFirestore.getInstance();

        usernameFromInput = findViewById(R.id.usernameFromFillIn);
        ageFromInput = findViewById(R.id.userAge);
        weightFromInput = findViewById(R.id.userWeight);
        heightFromInput = findViewById(R.id.userHeight);
        createAccButton = findViewById(R.id.createAccBtn);
        progressBar = findViewById(R.id.FillDetailsPB);
        errorMsg = findViewById(R.id.verifyEmailErrorMsg);
        resendBtn = findViewById(R.id.resendLinkBtn);
        reloadFab = findViewById(R.id.reloadFAB);

        Log.d("email verified ", String.valueOf(firebaseAuth.getCurrentUser().isEmailVerified()));

        user = firebaseAuth.getCurrentUser();
        reloadPage();

        createAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                createAccButton.setVisibility(View.INVISIBLE);
                String usernameForAcc = usernameFromInput.getText().toString().trim();
                String ageForAcc = ageFromInput.getText().toString().trim();
                String weightForAcc = weightFromInput.getText().toString().trim();
                String heightForAcc = heightFromInput.getText().toString().trim();

                if (!validateInputs(usernameForAcc, ageForAcc, weightForAcc, heightForAcc)) {
                    progressBar.setVisibility(View.INVISIBLE);
                    createAccButton.setVisibility(View.VISIBLE);
                    return;
                } else {
                    //Access collection from FireStore
                    DocumentReference documentReference = firestore.collection("users").document(CurrUserID);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("EmailID", currUserEmail);
                    map.put("UserName", usernameForAcc);
                    map.put("Age", ageForAcc);
                    map.put("Weight", weightForAcc);
                    map.put("Height", heightForAcc);
                    map.put("StepGoal", "2000");

                    HashMap<String, Object> weightMap = new HashMap<>();

                    String dt = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date()) + "0";
                    weightMap.put(dt, weightForAcc);
                    firestore.collection("weightReport").document(CurrUserID).set(weightMap);

                    //set key and val in map to database
                    documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(FillDetails.this, "Successful!!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(FillDetails.this, MainActivity.class);
                                startActivity(intent);

                                progressBar.setVisibility(View.INVISIBLE);
                                createAccButton.setVisibility(View.VISIBLE);
                                finish();
                            } else {

                                progressBar.setVisibility(View.INVISIBLE);
                                createAccButton.setVisibility(View.VISIBLE);
                                Toast.makeText(FillDetails.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }

            private boolean validateInputs(String username, String age, String weight, String height) {

                if (username.isEmpty()) {
                    usernameTIL.setError("Please enter your name / nickname");
                    return false;
                } else if (age.isEmpty()) {
                    ageTIL.setError("Please enter your age");
                    return false;
                } else if (weight.isEmpty()) {
                    weightTIL.setError("Please enter your weight");
                    return false;
                } else if (height.isEmpty()) {
                    heightTIL.setError("Please enter your height");
                    return false;
                } else return true;
            }
        });

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resendBtn.setVisibility(View.INVISIBLE);
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(FillDetails.this, "Verification mail sent", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            resendBtn.setVisibility(View.VISIBLE);
                        } else {
                            resendBtn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(FillDetails.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        reloadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });

    }

    public void reloadPage() {

        progressBar.setVisibility(View.VISIBLE);

        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {

                if (!task.isSuccessful())
                    Toast.makeText(FillDetails.this, "Request failed, retry later", Toast.LENGTH_SHORT).show();

                if (!user.isEmailVerified()) {

                    createAccButton.setVisibility(View.GONE);
                    resendBtn.setVisibility(View.VISIBLE);
                    errorMsg.setVisibility(View.VISIBLE);
                } else {

                    createAccButton.setVisibility(View.VISIBLE);
                    resendBtn.setVisibility(View.INVISIBLE);
                    errorMsg.setVisibility(View.GONE);
                    reloadFab.setVisibility(View.GONE);
                }

                progressBar.setVisibility(View.INVISIBLE);

            }
        });


    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please fill the details to complete your profile", Toast.LENGTH_SHORT).show();
    }
}