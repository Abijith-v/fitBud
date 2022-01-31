package com.example.fitnessapp.loginAndSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.MainActivity;
import com.example.fitnessapp.R;
import com.example.fitnessapp.workoutPage.MapsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialTextInputPicker;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class login extends AppCompatActivity {

    TextView signupPage, heading;
    TextInputEditText loginEmail, loginPassword;
    TextInputLayout emailTIL, passwordTIL;
    CircularProgressIndicator progressBar;
    MaterialButton loginButton;
    FirebaseAuth firebaseAuth;
    TextView forgotPass;
//    CheckBox checkBox;

    private AppUpdateManager appUpdateManager;
    private static final int REQUEST_APP_UPDATE = 11;

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        appUpdateManager = AppUpdateManagerFactory.create(this);
////        appUpdateManager.registerListener(listener);
//        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
//            @Override
//            public void onSuccess(AppUpdateInfo result) {
//                if(result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && result.isUpdateTypeAllowed(IMMEDIATE)) {
//
//                    requestUpdate(result, IMMEDIATE);
//
//                    InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
//                        @Override
//                        public void onStateUpdate(InstallState state) {
//                            if(state.installStatus() == InstallStatus.DOWNLOADED) {
//
//                                popupSnackBarForUpdateCompletion();
//                            }
//                        }
//                    };
//                }
//            }
//        });

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null && getIntent().getExtras() == null) {

            Log.d("userStatus", "already logged");

            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        Log.d("userStatus", "not logged in");
        SharedPreferences themeSP = this.getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);
        int themeKey = themeSP.getInt("nightModeOn", 0);

        loginEmail = findViewById(R.id.EmailForLogin);
        loginPassword = findViewById(R.id.passForLogin);
        loginButton = findViewById(R.id.buttonForLogin);
        signupPage = findViewById(R.id.moveToSignUp);
        progressBar = findViewById(R.id.loginPB);
        emailTIL = findViewById(R.id.TILforEmailInLogin);
        passwordTIL = findViewById(R.id.TILforPasswordInLogin);
        forgotPass = findViewById(R.id.forgotPassButton);
        heading = findViewById(R.id.loginPageHeading);

        if(themeKey == 2) {

            if(themeSP.getInt("nightModeOn", 0) == 2)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            ColorStateList white = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white));
            heading.setTextColor(white);
            loginEmail.setTextColor(white);
            emailTIL.setHintTextColor(white);

            loginPassword.setTextColor(white);
            passwordTIL.setHintTextColor(white);
            passwordTIL.setHintTextColor(white);

            forgotPass.setTextColor(white);
            signupPage.setTextColor(white);
            loginButton.setTextColor(white);

        }

        //login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);

                String emailForLogin = loginEmail.getText().toString();
                String passForLogin = loginPassword.getText().toString();

                if(emailForLogin.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    emailTIL.setError("please enter email");
                    return;
                }
                else emailTIL.setError(null);

                if(passForLogin.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    passwordTIL.setError("please enter pass");
                    return;
                }
                else passwordTIL.setError(null);

                //User Authentication
                firebaseAuth.signInWithEmailAndPassword(emailForLogin, passForLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            Intent intent = new Intent(login.this, MainActivity.class);
                            startActivity(intent);
//                            progressBar.setVisibility(View.INVISIBLE);
                            finish();
                        }
                        else {

                            progressBar.setVisibility(View.INVISIBLE);
                            loginButton.setVisibility(View.VISIBLE);
                            Toast.makeText(login.this, ""+task.getException(), Toast.LENGTH_SHORT).show();

                            return;
                        }
                    }
                });
            }
        });

        //new user signup
        signupPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
                finish();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog;
                final AlertDialog.Builder alert = new AlertDialog.Builder(login.this);

                View mview = getLayoutInflater().inflate(R.layout.info_dialog_layout, null);

                MaterialCardView bg = mview.findViewById(R.id.mapsDialogBG);
                MaterialTextView mainHeadingOfDialog = mview.findViewById(R.id.saveDialogMainHeading);
                MaterialTextView sideHeadingOfDialog = mview.findViewById(R.id.saveDialogSideHeading);
                MaterialCardView okBtn = mview.findViewById(R.id.saveButtonDialog);
                MaterialCardView cancelBtn = mview.findViewById(R.id.cancelBtnDialog);
                CircularProgressIndicator dialogPb = mview.findViewById(R.id.pbInSessionDialog);
                TextInputEditText ResetPassEmail = mview.findViewById(R.id.resetPassEmail);
                TextInputLayout emailTIL = mview.findViewById(R.id.TILforPassReset);

                emailTIL.setVisibility(View.VISIBLE);

                alert.setView(mview);
                alertDialog = alert.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                mainHeadingOfDialog.setText("Reset your password");
                sideHeadingOfDialog.setText("");

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = ResetPassEmail.getText().toString();

                        if(email.isEmpty()) {
                            emailTIL.setError("please enter email");
                        }
                        else {

                            dialogPb.setVisibility(View.VISIBLE);

                            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(login.this, "Successfu;", Toast.LENGTH_SHORT).show();
                                        sideHeadingOfDialog.setText("Password reset link has been sent to your email");

                                        emailTIL.setVisibility(View.GONE);
                                        okBtn.setVisibility(View.GONE);
                                        dialogPb.setVisibility(View.INVISIBLE);
                                    } else {
                                        Toast.makeText(login.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        dialogPb.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });

                        }

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

    private void popupSnackBarForUpdateCompletion() {

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "An update has been downloaded. ", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Restart FitBud", (View.OnClickListener) appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.BGcolor));
        snackbar.show();
    }

    private void requestUpdate(AppUpdateInfo result, int updateType) {

        try {
            appUpdateManager.startUpdateFlowForResult(result, updateType, login.this, REQUEST_APP_UPDATE);
        }
        catch(IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
}