package com.example.fitnessapp.workoutPage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.example.fitnessapp.cards.stepsCard;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public int LOC_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;
    boolean mapEnabled = false;

    RelativeLayout mapContainerLayout;
    CircularProgressIndicator progressIndicator;

    MaterialTextView stepCountInBottom, distanceInBottom, caloriesInBottom, durationInBottom;
    MaterialButton resetBtn;
    MaterialCardView stopBtn, pauseBtn;
    ImageView pauseIcon;

    SharedPreferences pref;
    SharedPreferences.OnSharedPreferenceChangeListener listenerInStepsCard;

    private int stepCount = 0, weight = -1;
    private float distance = 0;
    private long extraTime = 0;

    int realSteps;
    float realDistance;

    boolean paused = false;
    Chronometer stopwatch;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    private Polyline polyline_path;

    private int finalSteps;
    private int themeKey;
    private float finalDistance;
    private String startTime = "", currDate = "";
    private Boolean firstTimeCam = true;

    AlertDialog alertDialog;
    DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_frag);
        supportMapFragment.getMapAsync(this);


        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        progressIndicator = findViewById(R.id.mapProgessBar);
        mapContainerLayout = findViewById(R.id.fragLayout);

        currDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        startTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        stepCountInBottom = findViewById(R.id.stepCountInMaps);
        stopwatch = findViewById(R.id.stopwatchInBottom);
        caloriesInBottom = findViewById(R.id.caloriesBurntMap);
        distanceInBottom = findViewById(R.id.distanceWalkedMap);
        pauseBtn = findViewById(R.id.pauseButtonMaps);
        stopBtn = findViewById(R.id.stopButtonMaps);
        pauseIcon = findViewById(R.id.pauseIconMaps);
        resetBtn = findViewById(R.id.resetBtnMaps);
        SharedPreferences themeSP = this.getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);

        // set the color to black

        themeKey = themeSP.getInt("nightModeOn", 0);
        if(themeKey == 2) {
            RelativeLayout bottomLayout = findViewById(R.id.bottomSheetInMap);
            bottomLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.mildBlack)));
        }
        stopwatch.setTypeface(ResourcesCompat.getFont(this, R.font.viga));

        //bottom sheets
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLaoutMaps);
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottomSheetInMap);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(320);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if(firebaseAuth.getCurrentUser() == null) return;

        //lets get weight from firebase first
        DocumentReference documentReference = firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot documentSnapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                if(firebaseAuth.getCurrentUser() == null) return;

                String weightInStr = documentSnapshot.getString("Weight");
                weight = Integer.parseInt(weightInStr);
            }
        });


        //set up fused location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //get steps from pref
        pref = this.getSharedPreferences("stepCounter", Context.MODE_PRIVATE);
        realSteps = pref.getInt("steps", 0);
        realDistance = pref.getFloat("distance", 0);

        startSharedPrefListener();

        //pause btn
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!paused) {
                    stopwatch.stop();
                    extraTime = SystemClock.elapsedRealtime() - stopwatch.getBase();
                    pauseIcon.setImageResource(R.drawable.play_icon);

                    pref.unregisterOnSharedPreferenceChangeListener(listenerInStepsCard);
                } else {

                    stopwatch.setBase(SystemClock.elapsedRealtime() - extraTime);
                    stopwatch.start();
                    pauseIcon.setImageResource(R.drawable.pause_icon);

                    startSharedPrefListener();
                }

                paused = !paused;
            }
        });

        //stop btn
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraTime = 0;
                saveSessionToFirebase();
            }
        });

        //reset btn
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realSteps = pref.getInt("steps", 0);
                realDistance = pref.getFloat("distance", 0);

                extraTime = 0;
                stopwatch.setBase(SystemClock.elapsedRealtime());
            }
        });

    }

    private void saveSessionToFirebase() {

        String endTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        final AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);

        View mview = getLayoutInflater().inflate(R.layout.info_dialog_layout, null);

        MaterialCardView bg = mview.findViewById(R.id.mapsDialogBG);
        MaterialTextView mainHeadingOfDialog = mview.findViewById(R.id.saveDialogMainHeading);
        MaterialTextView sideHeadingOfDialog = mview.findViewById(R.id.saveDialogSideHeading);
        MaterialCardView okBtn = mview.findViewById(R.id.saveButtonDialog);
        MaterialCardView cancelBtn = mview.findViewById(R.id.cancelBtnDialog);
        CircularProgressIndicator pb = mview.findViewById(R.id.pbInSessionDialog);

        if(themeKey == 2)
            bg.setCardBackgroundColor(ContextCompat.getColor(this, R.color.mildBlack));


        alert.setView(mview);
        alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        if (stepCount >= 0) {

            String tempCal = df.format((weight * 0.708) * ((stepCount * 60.0) / 100000));
            String distStrWhileSaving = df.format(distance);

            mainHeadingOfDialog.setText("End session now?");
            sideHeadingOfDialog.setText("distance - " + String.valueOf(distStrWhileSaving) + " KM");

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pb.setVisibility(View.VISIBLE);

                    // Save session to firebase
                    DocumentReference documentReference = firestore.collection("walkSessions").document(firebaseAuth.getCurrentUser().getUid());
                    HashMap<String, Object> map = new HashMap<>();

                    String valueToStore = currDate + " " + startTime + " " + endTime + " " + stepCount + " " + distStrWhileSaving + " " + tempCal;
                    map.put(startTime, valueToStore);

                    documentReference.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                pb.setVisibility(View.INVISIBLE);
                                Toast.makeText(MapsActivity.this, "Session complete", Toast.LENGTH_SHORT).show();

                                alertDialog.dismiss();
                                finish();

                            } else {
                                Toast.makeText(MapsActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();

                                alertDialog.dismiss();
                                finish();
                            }
                        }
                    });
                }
            });


        } else {

            mainHeadingOfDialog.setText("Failed to save");
            sideHeadingOfDialog.setText("Very small distance covered, Exit?");
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    finish();
                }
            });
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void startSharedPrefListener() {

        listenerInStepsCard = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {


                if (key.equals("steps")) {

                    //exception : when day changes, steps are reset
                    int currSteps = prefs.getInt("steps", 0);

                    if (currSteps == 0) {
                        realSteps = 0;
                        realDistance = 0;
                    }

                    stepCount = Math.abs(currSteps - realSteps);
                    distance = Math.abs(prefs.getFloat("distance", 0) - realDistance);

                    String distStr = df.format(distance);
                    stepCountInBottom.setText(String.valueOf(stepCount));
                    distanceInBottom.setText(distStr + " KM");

                    String calStr = df.format((weight * 0.708) * ((stepCount * 60.0) / 100000));
                    caloriesInBottom.setText(calStr + " KC");
                }
            }
        };
        pref.registerOnSharedPreferenceChangeListener(listenerInStepsCard);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mapEnabled) {
            setupLoc();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mapEnabled = true;
        setupLoc();

        PolylineOptions routes = new PolylineOptions().width(25).color(ContextCompat.getColor(this, R.color.BGcolor));
        polyline_path = mMap.addPolyline(routes);

        stopwatch.start();
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult == null) {
                Toast.makeText(MapsActivity.this, "null", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Location l : locationResult.getLocations()) {

                LatLng latLng = new LatLng(l.getLatitude(), l.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);

                if (firstTimeCam) {
                    mMap.moveCamera(cameraUpdate);

                    mapContainerLayout.setVisibility(View.VISIBLE);
                    progressIndicator.setVisibility(View.INVISIBLE);

                    firstTimeCam = false;
                } else {
                    mMap.animateCamera(cameraUpdate);
                }

                UpdatePoints(latLng);
            }

        }
    };

    private void UpdatePoints(LatLng newlatlng) {
        List<LatLng> points = polyline_path.getPoints();
        points.add(newlatlng);
        polyline_path.setPoints(points);
    }

    private void setupLoc() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            checkSettingsAndZoomToUser();
        } else
            askPermission();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOC_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
                checkSettingsAndZoomToUser();
            } else {
                askPermission();
            }
        }
    }

    public void askPermission() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) <= -1) {

            Toast.makeText(this, "Please allow location permission in the App's settings", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);

            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQUEST_CODE);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void checkSettingsAndZoomToUser() {

        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(locationSettingsRequest);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // GPS is enabled

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {


                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MapsActivity.this, 999);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }


    @Override
    public void onBackPressed() {
    }
}