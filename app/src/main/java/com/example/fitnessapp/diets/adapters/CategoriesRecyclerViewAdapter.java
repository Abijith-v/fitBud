package com.example.fitnessapp.diets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp.R;
import com.example.fitnessapp.diets.Categories;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.reflect.KCallable;

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.RecyclerViewHolder> {

    private List<Categories.Category> categories;
    private Context context;
    private static ClickListener clickListener;

    public CategoriesRecyclerViewAdapter(List<Categories.Category> cat, Context con) {
        this.categories = cat;
        this.context = con;
    }

    @NonNull
    @Override
    public CategoriesRecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_for_categories, viewGroup, false);
        return new RecyclerViewHolder(view);
    } //make a new viewholder class which will access the layout widgets with butterknife

    @Override
    public void onBindViewHolder(@NonNull CategoriesRecyclerViewAdapter.RecyclerViewHolder viewHolder, int i) {

        String imgURL = categories.get(i).getStrCategoryThumb();
        Picasso.get().load(imgURL).placeholder(R.drawable.semi_round_button).into(viewHolder.categoryPic, new Callback() {
            @Override
            public void onSuccess() {
                viewHolder.categoriesProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                viewHolder.categoriesProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "Failed to load Categories images", Toast.LENGTH_SHORT).show();
            }
        });

        String categoryNameString = categories.get(i).getStrCategory();

        if(categoryNameString.length() > 7) viewHolder.categoryName.setTextSize(10);
        else if(categoryNameString.length() > 10) viewHolder.categoryName.setTextSize(6);
        else if(categoryNameString.length() > 15) viewHolder.categoryName.setTextSize(4);
        else if(categoryNameString.length() > 20) viewHolder.categoryName.setTextSize(2);

        viewHolder.categoryName.setText(categoryNameString);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // use butterKnife for view
        @BindView(R.id.categoriesPB)
        CircularProgressIndicator categoriesProgressBar;
        @BindView(R.id.categoryPic)
        ImageView categoryPic;
        @BindView(R.id.nameOfCategory)
        TextView categoryName;
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
        CategoriesRecyclerViewAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }
}
