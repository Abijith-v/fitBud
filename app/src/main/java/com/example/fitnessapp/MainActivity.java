package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fitnessapp.HomeScreen.HomeFrag;
import com.example.fitnessapp.HomeScreen.healthFrag;
import com.example.fitnessapp.HomeScreen.moreFrag;
import com.example.fitnessapp.HomeScreen.reportsFrag;
import com.example.fitnessapp.diets.recipeHomePage;
import com.example.fitnessapp.itemsInMore.profile;
import com.example.fitnessapp.loginAndSignup.FillDetails;
import com.example.fitnessapp.loginAndSignup.onBoardingScreen;
import com.example.fitnessapp.workoutPage.MapsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    ChipNavigationBar bottomNav;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    CircularProgressIndicator pb;

    MaterialCardView userInfoBtn;

    @Override
    public void onResume() {
        super.onResume();

//        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFrag()).commitAllowingStateLoss();
//        bottomNav.setItemSelected(R.id.bottom_nav_home, true);
        selectBottomMenuFrag();
    }

    public void onPause() {
        super.onPause();
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences themeSP = this.getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);

        if(themeSP.getInt("nightModeOn", 0) == 2)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        //firestore and auth
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null) return;

        checkPermission();

        //Unable to start service Intent
        //BG service
        Intent serviceIntent = new Intent(this, BGservice.class);
        startForegroundService(serviceIntent);

        bottomNav = findViewById(R.id.bottomNavMenu);
        pb = findViewById(R.id.mainPagePB);
        bottomNav.setItemSelected(R.id.bottom_nav_home, true);

        userInfoBtn = findViewById(R.id.userProfileButton);

        //Check if user details are there in fireStore, Else redirect to FillDetails
        String UserID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(UserID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                if(!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!task.getResult().exists()) {

                    //Implement a dialog box here

                    pb.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Fill details first", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, FillDetails.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    userInfoBtn.setVisibility(View.VISIBLE);
                    bottomNav.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.INVISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFrag()).commitAllowingStateLoss();
                    selectBottomMenuFrag();
                }
            }
        });

        //BUTTONS

        userInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, profile.class));
            }
        });
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P ) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("This permission is for tracking your steps")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
                else
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void selectBottomMenuFrag() {

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                Fragment frag = null;
                switch(i) {
                    case R.id.bottom_nav_home:
                        frag = new HomeFrag();
                        break;
                    case R.id.bottom_nav_diet:
                        frag = new healthFrag();
                        break;
                    case R.id.bottom_nav_reports:
                        frag = new reportsFrag();
                        break;
                    case R.id.bottom_nav_settings:
                        frag = new moreFrag();
                        break;
                }
                //set frag to the relative layout with id frag_container
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_container, frag).commit();
            }
        });
    }

}