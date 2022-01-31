package com.example.fitnessapp.workoutPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class sessionAdapter extends RecyclerView.Adapter<sessionAdapter.sessionViewHolder> {

    public class sessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView heading;
        public MaterialTextView cal, steps, distance, startTime, endTime;
        public MaterialCardView mainCard;
        public LinearLayout detailsLayout;
        public ImageView dropdownButton;
        public boolean expanded = false;

        public sessionViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            heading = itemView.findViewById(R.id.HeadingSessionItem);
            cal = itemView.findViewById(R.id.caloriesSessionItem);
            steps = itemView.findViewById(R.id.stepSessionItem);
            distance = itemView.findViewById(R.id.distanceSessionItem);
            startTime = itemView.findViewById(R.id.startTimeSessionItem);
            endTime = itemView.findViewById(R.id.endTimeSessionItem);
            mainCard = itemView.findViewById(R.id.mainLayoutSessionItem);
            dropdownButton = itemView.findViewById(R.id.dropdownImg);
            detailsLayout = itemView.findViewById(R.id.detailsSessionLL);
        }

        @Override
        public void onClick(View v) {
            if (onEntryClickListener != null) {
                onEntryClickListener.onEntryClick(v, getLayoutPosition());
            }
        }
    }


    private ArrayList<String> ar = new ArrayList<>();
    private Context context;
    private OnEntryClickListener onEntryClickListener;

    sessionAdapter(Context c, ArrayList<String> Ar) {
        ar = Ar;
        context = c;
    }

    @NonNull
    @NotNull
    @Override
    public sessionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        // single list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_srssion_recyclerview, parent, false);
        return new sessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull sessionViewHolder holder, int position) {

        SharedPreferences themeSP = context.getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);
        int themeKey = themeSP.getInt("nightModeOn", 0);

        String s = ar.get(position);
        s += " ";

        ArrayList<String> details = new ArrayList<>();
        String curr = "";
        for(char c : s.toCharArray()) {
            if(c == ' ') {
                details.add(curr);
                curr = "";
            }
            else
                curr += c;
        }

        if(themeKey == 2) {
            ColorStateList white = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white));

            holder.heading.setTextColor(white);
            holder.cal.setTextColor(white);
            holder.startTime.setTextColor(white);
            holder.endTime.setTextColor(white);
            holder.steps.setTextColor(white);
            holder.distance.setTextColor(white);
        }

        // | 0 - date | 1 - startTime | 2 - endTime | 3 - steps | 4 - distance | 5 - cal
        holder.heading.setText(String.valueOf(position + 1) + ". Session on - " + details.get(0));
        holder.startTime.setText(details.get(1));
        holder.endTime.setText(details.get(2));
        holder.steps.setText(details.get(3));
        holder.distance.setText(details.get(4) + "KM");
        holder.cal.setText(details.get(5) + " KCAL");

        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.dropdownButton.setImageResource(holder.detailsLayout.getVisibility() == View.GONE ? R.drawable.up_icon : R.drawable.down_icon);
                holder.detailsLayout.setVisibility(holder.detailsLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ar.size();
    }


    public interface OnEntryClickListener {
        void onEntryClick(View view, int position);
    }

    public void setOnEntryClickListener(OnEntryClickListener oec) {
        onEntryClickListener = oec;
    }
}
