package com.example.fitnessapp.loginAndSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialTextInputPicker;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class signup extends AppCompatActivity {

    TextView gotoLogin, title;
    MaterialButton signUpBtn;
    TextInputEditText emailID, password;
    TextInputLayout emailTIL, passTIL;
    CircularProgressIndicator progressBar;
    //Firebase auth
    FirebaseAuth firebaseAuth;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        gotoLogin = findViewById(R.id.goBackToLogin);
        signUpBtn = findViewById(R.id.signUpButton);
        emailID = findViewById(R.id.signupEmailID);
        password = findViewById(R.id.signupPass);
        backBtn = findViewById(R.id.signupBackBtn);
        progressBar = findViewById(R.id.signUpPB);;
        emailTIL = findViewById(R.id.TILforEmailSignup);
        passTIL = findViewById(R.id.TILforPassSignup);
        title = findViewById(R.id.signupTitle);

        SharedPreferences themeSP = this.getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);
        int themeKey = themeSP.getInt("nightModeOn", 0);

        if(themeKey == 2) {

            ColorStateList white = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white));
            title.setTextColor(white);
            gotoLogin.setTextColor(white);
            emailTIL.setHintTextColor(white);
            passTIL.setHintTextColor(white);
            emailID.setTextColor(white);
            password.setTextColor(white);
            signUpBtn.setTextColor(white);
        }

            //sign up button =
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailFromInput = emailID.getText().toString();
                String passFromInput = password.getText().toString();

                if(emailFromInput.isEmpty()) {
                    emailTIL.setError("please enter email");
                    return;
                }
                if(passFromInput.isEmpty()) {
                    passTIL.setError("please enter pass");
                    return;
                }

                signUpBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(emailFromInput, passFromInput).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            FirebaseUser currUser = firebaseAuth.getCurrentUser();
                            currUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {

                                    if(task.isSuccessful()) {

                                        Toast.makeText(signup.this, "Verification mail sent", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(signup.this, FillDetails.class);

                                        progressBar.setVisibility(View.INVISIBLE);
                                        startActivity(intent);
                                    }
                                    else {

                                        progressBar.setVisibility(View.INVISIBLE);
                                        signUpBtn.setVisibility(View.VISIBLE);
                                        Toast.makeText(signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            signUpBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        //goto login
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, login.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}