package com.example.fitnessapp.HomeScreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.utils.Utils;
import com.example.fitnessapp.BGservice;
import com.example.fitnessapp.MainActivity;
import com.example.fitnessapp.R;
import com.example.fitnessapp.cards.stepsCard;
import com.example.fitnessapp.diets.adapters.featuredViewPager;
import com.example.fitnessapp.diets.recipeHomePage;
import com.example.fitnessapp.itemsInMore.profile;
import com.example.fitnessapp.loginAndSignup.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.internal.Util;

public class moreFrag extends Fragment {

    class settingsAdapter extends ArrayAdapter<String> {

        Context context;
        String Title[];
        int Img[], theme;

        settingsAdapter(Context c, String t[], int I[], int currTheme) {
            super(c, R.layout.settings_list_singleitem, t);
            this.context = c;
            this.Title = t;
            this.Img = I;
            theme = currTheme;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.settings_list_singleitem, parent, false);
            ImageView image = row.findViewById(R.id.imgLeftSettings);
            TextView title = row.findViewById(R.id.settingItemTitle);

            if(theme == 2)
                title.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));

            image.setImageResource(Img[position]);
            title.setText(Title[position]);

            return row;
        }
    }

    ListView listView, listView2;
    MaterialCardView recipeCard;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    MaterialButton theme1Button;
    SwitchMaterial nightModeSwitch;
    SharedPreferences sharedPreferences;

    TextView heading1, subHeading1, heading2;

    Boolean lightTheme = true;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_more, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);
        int themeKey = sharedPreferences.getInt("nightModeOn", 0);

        nightModeSwitch = view.findViewById(R.id.themeSwitch);
        listView = view.findViewById(R.id.settingsListView);
        recipeCard = view.findViewById(R.id.recipeCardInSettings);
        heading1 = view.findViewById(R.id.moreRecipeHeading1);
        heading2 = view.findViewById(R.id.tryDarkModeHeading);
        subHeading1 = view.findViewById(R.id.moreSubHeadingRecipe);

        if(themeKey == 2) {
            ColorStateList white = ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white));

            heading1.setTextColor(white);
            heading2.setTextColor(white);
            subHeading1.setTextColor(white);
        }

        recipeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), recipeHomePage.class));
            }
        });

        String[] titles1 = {"Profile", "Edit daily goal", "Logout", "About"};
        int[] images1 = {R.drawable.user_icon, R.drawable.target_icon, R.drawable.exit_icon, R.drawable.info_logo};
        settingsAdapter adapter1 = new settingsAdapter(getActivity(), titles1, images1, themeKey);

        listView.setScrollContainer(false);
        listView.setAdapter(adapter1);

        // 0 - default - light
        // 1 - set to light
        // 2 - set to dark

        if (themeKey == 0 || themeKey == 1) {
            sharedPreferences.edit().putInt("nightModeOn", 1).apply();
            nightModeSwitch.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.lightGrey), PorterDuff.Mode.SRC_IN);
        }
        else if (themeKey == 2) {
            nightModeSwitch.setChecked(true);
            nightModeSwitch.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.BGcolor), PorterDuff.Mode.SRC_IN);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent;
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), profile.class));
                        break;

                    case 1:
                        //daily goal
                        editGoalDialog();
                        break;

                    case 2:

                        intent = new Intent(getActivity(), login.class);
//                    intent.putExtra("userLoggedOut", 1);

                        getActivity().stopService(new Intent(getActivity(), BGservice.class));

                        startActivity(intent);
                        getActivity().finish();

                        firebaseAuth.signOut();
                        break;

                    case 3:
                        aboutDialog(themeKey);
                        break;

                }

            }
        });

        Log.d("Theme", String.valueOf(themeKey));

        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPreferences.edit().putInt("nightModeOn", 2).apply();
                    getActivity().recreate();
                } else {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPreferences.edit().putInt("nightModeOn", 1).apply();
                    getActivity().recreate();
                }
            }
        });

//        theme1Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                getActivity().getTheme().applyStyle(R.style.theme1_dark, true);
//                getActivity().recreate();
//            }
//        });

        return view;
    }

    public void aboutDialog(int themeKey) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        View mview = getLayoutInflater().inflate(R.layout.info_dialog_layout, null);

        MaterialCardView bg = mview.findViewById(R.id.mapsDialogBG);
        MaterialTextView mainHeadingOfDialog = mview.findViewById(R.id.saveDialogMainHeading);
        MaterialTextView sideHeadingOfDialog = mview.findViewById(R.id.saveDialogSideHeading);
        MaterialCardView okBtn = mview.findViewById(R.id.saveButtonDialog);
        MaterialCardView cancelBtn = mview.findViewById(R.id.cancelBtnDialog);
//        CircularProgressIndicator pb = mview.findViewById(R.id.pbInSessionDialog);

        okBtn.setVisibility(View.GONE);

        if(themeKey == 2)
            bg.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.mildBlack));
        else
            bg.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));

        alert.setView(mview);
        AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        mainHeadingOfDialog.setText("About FitBud");
        sideHeadingOfDialog.setText("FitBud is simple fitness tracking app which will keep track of your weekly data \n \n Version : 1.0");
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    public void editGoalDialog() {
        SharedPreferences themeSP = getActivity().getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);

        //launch dialog to ask user to input new step goal

        final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        View mview = getLayoutInflater().inflate(R.layout.activity_edit_step_goal_dialog, null);


        TextInputEditText newStepGoalEditText = mview.findViewById(R.id.stepGoalFromEditText);
        TextInputLayout TILforSpteGoal = mview.findViewById(R.id.textInputLayoutinStepsDialog);
        MaterialButton confirmBtn = mview.findViewById(R.id.confirmBtninUpdateStepGoal);
        MaterialButton cancelBtn = mview.findViewById(R.id.cancelBtninUpdateStepGoal);
        CircularProgressIndicator circularPB = mview.findViewById(R.id.confirmBtnPBinUpdateStepGoal);
        RelativeLayout layout = mview.findViewById(R.id.editGoalLayout);

        if (themeSP.getInt("nightModeOn", 0) == 2)
            layout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.mildBlack)));
        else
            layout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));

        DocumentReference documentReference = firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());

        alert.setView(mview);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newStepGoalStr = newStepGoalEditText.getText().toString();

                if (newStepGoalStr.isEmpty()) {
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

                        if (task.isSuccessful()) {
                            //display tick animation
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(), "New step goal set!!", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
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

}