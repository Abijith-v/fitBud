package com.example.fitnessapp.diets;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.fitnessapp.diets.API.client;
import com.example.fitnessapp.diets.API.foodAPI;

public class misc {

    public static foodAPI getApi() {
        return client.getclient().create(foodAPI.class);
    }

    public static AlertDialog showDialogMessage(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message).show();
        if (alertDialog.isShowing())
            alertDialog.cancel();
        return alertDialog;
    }
}
