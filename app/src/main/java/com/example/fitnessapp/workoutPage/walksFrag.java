package com.example.fitnessapp.workoutPage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class walksFrag extends Fragment {

    MaterialCardView startSessionButton;
    RelativeLayout layout;
    MaterialTextView quoteTV;
    CircularProgressIndicator quotePB;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    TextView startSessionHeading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_walks, container, false);
        SharedPreferences themeSP = getActivity().getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        startSessionButton = view.findViewById(R.id.startSessionBtn);
        layout = view.findViewById(R.id.walkSessionLayout);
        quoteTV = view.findViewById(R.id.startSessionQuote);
        quotePB = view.findViewById(R.id.sessionQuotePB);
        startSessionHeading = view.findViewById(R.id.startNewSessionHeading);

        quoteTV.setText("");

        firestore.collection("quotes").document("session").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                if(!task.isSuccessful()) return;

                DocumentSnapshot documentSnapshot = task.getResult();
                Map<String, Object> map = documentSnapshot.getData();

                Random random = new Random();
                int index = random.nextInt(map.size());

                quoteTV.setText(String.valueOf(map.get(String.valueOf(index))));
                quotePB.setVisibility(View.INVISIBLE);
            }
        });

        int themeKey = themeSP.getInt("nightModeOn", 0);
        if(themeKey != 2)
            layout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
        else
            startSessionHeading.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        startSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
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

                mainHeadingOfDialog.setText("Start session?");
                sideHeadingOfDialog.setText("Please make sure your device location is turned on");

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        startActivity(intent);
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

        return view;
    }
}