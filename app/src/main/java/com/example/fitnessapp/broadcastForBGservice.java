package com.example.fitnessapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class broadcastForBGservice extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {
            Intent serviceIntent = new Intent(context, BGservice.class);
            context.startForegroundService(serviceIntent);
            Log.i("reboot complete", "serviec restarted");
        }
    }
}
