package com.example.fitnessapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.fitnessapp.loginAndSignup.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BGservice extends Service implements SensorEventListener {

    //sensors
    SensorManager sensorManager;
    Sensor sensor;

    //the real step Count
    public int stepCount = 0;

    //Firestore and fireauth
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fireStore;
    String currUID;

    boolean savingPrevDaySteps = false;

    DecimalFormat df = new DecimalFormat();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

//        FirebaseApp.initializeApp(this);

        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Log.d("BG service : ", "Started");
        stepCount = getApplicationContext()
                    .getSharedPreferences("stepCounter", Context.MODE_PRIVATE)
                    .getInt("steps", 0);

        SharedPreferences pref = this.getSharedPreferences("stepCounter", Context.MODE_PRIVATE);

//        pref.edit().putInt("day", Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        pref.edit().putFloat("distance", 0); // assume 60 to be step distance
        pref.edit().putFloat("calories", 0);

        currUID = firebaseAuth.getCurrentUser().getUid();
        df.setMaximumFractionDigits(2);

        register();
        updateSteps();
        showNotif();

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateSteps()  {

        SharedPreferences preferences =  getApplicationContext().
                                         getSharedPreferences("stepCounter", Context.MODE_PRIVATE);
        preferences.edit().putInt("steps", stepCount).apply();

        float dist = ((stepCount*(float)60)/100)/1000;
        preferences.edit().putFloat("distance", Float.parseFloat(df.format(dist))).apply();
        showNotif();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotif() {

        String NOTIF_CHANNEL_ID = "com.example.fitnessapp";
        String ChannelName = "FitBud";
        NotificationChannel notificationChannel = new NotificationChannel(NOTIF_CHANNEL_ID, ChannelName, NotificationManager.IMPORTANCE_LOW);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(notificationChannel);

        SharedPreferences preferences = this.getSharedPreferences("stepCounter", Context.MODE_PRIVATE);
        int stepsFromPref = preferences.getInt("steps", 0);
        float distanceInKM = preferences.getFloat("distance", 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID).setVibrate(new long[]{0L});

        SpannableString steps = new SpannableString("STEPS - " + String.valueOf(stepsFromPref));
        SpannableString dist = new SpannableString("DIST - " + String.valueOf(distanceInKM + " KM"));

        steps.setSpan(new StyleSpan(Typeface.BOLD), 0, steps.length(), 0);
        dist.setSpan(new StyleSpan(Typeface.BOLD), 0, dist.length(), 0);

        contentView.setTextViewText(R.id.stepsInNotif, steps);
        contentView.setTextViewText(R.id.distInNotif, dist);
//        contentView.setT

//        Intent notifIntent = new Intent(this, BGservice.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, login.class), 0);
        notificationBuilder.setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.setSmallIcon(R.drawable.walk_image)
                .setOngoing(true)
                .setCustomContentView(contentView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setShowWhen(false)
                .setColor(ContextCompat.getColor(this, R.color.BGcolor))
                .setColorized(true)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .build();

        startForeground(1, notification);
    }


    private void register() {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        else
            Toast.makeText(this, "No sensor detected", Toast.LENGTH_LONG).show();

        try {
            sensorManager.unregisterListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();

//        Intent serviceIntent = new Intent(this, BGservice.class);
//        startForegroundService(serviceIntent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Log.d("BS service task : ", "Removed ");

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, PendingIntent.getService(this, 3, new Intent(this, BGservice.class), 0));
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onSensorChanged(SensorEvent event) {

        //Toast.makeText(this, "changed", Toast.LENGTH_SHORT).show();

        SharedPreferences preferences =  getApplicationContext().getSharedPreferences("stepCounter", Context.MODE_PRIVATE);

        stepCount = preferences.getInt("steps", 0) + 1;
        updateSteps();

        int currentDayNumInInteger = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        //get day in pref
        int prefDay = preferences.getInt("day", -1);

        if(savingPrevDaySteps) return;

        //check if its a new day
        if(currentDayNumInInteger != prefDay) {

            savingPrevDaySteps = true;

            LocalDate localDate = LocalDate.now();
            int dayNum = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            int prevDay = (dayNum - 1 + 7) % 7;

            //Set up new collection in firestore for weekly steps
            DocumentReference documentReference = fireStore.collection("weeklySteps").document(currUID);
            HashMap<String, Object> map = new HashMap<>();

            map.put(String.valueOf(prevDay), stepCount);

            //set map to firestore collection : "weeklySteps"
            documentReference.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {

                    //reset step count and set new day num
                    preferences.edit().putInt("steps", 1).apply();
                    preferences.edit().putFloat("distance", 0).apply();
                    preferences.edit().putInt("day", currentDayNumInInteger).apply();

                    savingPrevDaySteps = false;
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }


}
