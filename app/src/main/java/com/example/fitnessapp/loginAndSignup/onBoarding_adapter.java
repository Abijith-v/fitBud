package com.example.fitnessapp.loginAndSignup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.example.fitnessapp.R;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class onBoarding_adapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public onBoarding_adapter(Context c) {
        context = c;
    }

    int animations[] = {R.raw.walking, R.raw.graph, R.raw.location};
    String titles[] = {"Daily tracking", "View reports", "Sessions"};
    String descriptions[] = {"Keep track of daily steps, calories burnt, distance travelled.",
                             "Keep track of your weekly step counts and weight",
                             "Setup walking sessions to improve your daily step count"};


    @Override
    public int getCount() {
        return titles.length;
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboarding_slide_template, container, false);

        TextView heading = view.findViewById(R.id.titleInOnboard);
        TextView desc = view.findViewById(R.id.descInOnboard);
        LottieAnimationView lottie = view.findViewById(R.id.lottieInOnboard);

        heading.setText(titles[position]);
        desc.setText(descriptions[position]);
        lottie.setAnimation(animations[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == (RelativeLayout) object;
    }
}
