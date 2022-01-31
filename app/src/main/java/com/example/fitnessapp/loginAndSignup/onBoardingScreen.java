package com.example.fitnessapp.loginAndSignup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fitnessapp.R;
import com.google.android.material.button.MaterialButton;

public class onBoardingScreen extends AppCompatActivity {

    ViewPager viewPager;
    onBoarding_adapter adapter;
    LinearLayout dotsLayout;
    MaterialButton letsGoBtn;
    Animation animation;

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpDots(position);

            if(position == 2) {
                animation = AnimationUtils.loadAnimation(onBoardingScreen.this, R.anim.onboarding_btn_animation);
                letsGoBtn.setAnimation(animation);
                letsGoBtn.setVisibility(View.VISIBLE);
            }
            else
                letsGoBtn.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("showOnBoarding", MODE_PRIVATE);
        Boolean showScreen = sharedPreferences.getBoolean("firstTime", true);

        if(!showScreen) {

            startActivity(new Intent(onBoardingScreen.this, login.class));
            finish();
        }
        sharedPreferences.edit().putBoolean("firstTime", false).apply();


        viewPager = findViewById(R.id.viewPagerOnboard);
        dotsLayout = findViewById(R.id.dotsLayout);
        letsGoBtn = findViewById(R.id.letsGoOnboardingBtn);

        setUpDots(0);
        adapter = new onBoarding_adapter(this);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(changeListener);

        letsGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(onBoardingScreen.this, login.class));
                finish();
            }
        });
    }

    private void setUpDots(int pos) {

        //add dots
        dotsLayout.removeAllViews();
        for(int i = 0; i < 3; i++) {

            TextView dot = new TextView(this);
            dot.setText(Html.fromHtml("&#8226;"));
            dot.setTextSize(40);
            dot.setTextColor(getResources().getColor(R.color.darkgreen));
            
            if(i == pos)
                dot.setTextColor(getResources().getColor(R.color.white));
            dotsLayout.addView(dot);
        }

    }
}