package com.example.fitnessapp.diets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.CircularIntArray;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp.R;
import com.example.fitnessapp.diets.Food;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewEachCategory extends RecyclerView.Adapter<RecyclerViewEachCategory.RecyclerViewHolder> {

    private List<Food.Meal> meals;
    private Context context;
    private static ClickListener clickListener;

    public RecyclerViewEachCategory(Context context, List<Food.Meal> meals) {
        this.meals = meals;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerViewEachCategory.RecyclerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_recyclerview_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewEachCategory.RecyclerViewHolder holder, int position) {


        String bgPicCategory = meals.get(position).getStrMealThumb();
        Picasso.get().load(bgPicCategory).into(holder.BGpic, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressIndicator.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Failed to load image(s)", Toast.LENGTH_SHORT).show();
            }
        });

        String strMealName = meals.get(position).getStrMeal();
        holder.mealName.setText(strMealName);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.mealThumb)
        ImageView BGpic;
        @BindView(R.id.mealName)
        TextView mealName;
        @BindView(R.id.RVcategoryItemPB)
        CircularProgressIndicator progressIndicator;

        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewEachCategory.clickListener = clickListener;
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }
}

