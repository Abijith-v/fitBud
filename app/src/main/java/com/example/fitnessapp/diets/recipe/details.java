package com.example.fitnessapp.diets.recipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.example.fitnessapp.diets.Food;
import com.example.fitnessapp.diets.misc;
import com.facebook.shimmer.Shimmer;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class details extends AppCompatActivity implements recipeInterface{

    AppBarLayout appBarLayout;
    ImageView BGpic;
    MaterialCardView backBtn, videoButton, sourceButton;
    TextView category, instructions, ingredients, title;
    CircularProgressIndicator picPB;
    View shimmerInDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        BGpic = findViewById(R.id.recipeDetailTopPic);
        videoButton = findViewById(R.id.YTbutton);
        sourceButton = findViewById(R.id.sourceBtn);
        backBtn = findViewById(R.id.backButtonDetailsPage);
        title = findViewById(R.id.actionbarTitle);
        category = findViewById(R.id.categoryInDetails);
        instructions = findViewById(R.id.instructionsTextView);
        ingredients = findViewById(R.id.ingredientsList);
        picPB = findViewById(R.id.bgPicProgressbar);
        shimmerInDetails = findViewById(R.id.recipeDetailsShimmer);

        Intent intent = getIntent();
        String foodName = intent.getExtras().getString("foodNameFromHealthFrag");

        recipeSetter setter = new recipeSetter(this);
        setter.getMealById(foodName);
    }

    @Override
    public void showLoading() {
        shimmerInDetails.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        shimmerInDetails.setVisibility(View.GONE);
    }

    @Override
    public void setMeals(Food.Meal meal) {

        Picasso.get().load(meal.getStrMealThumb()).into(BGpic, new Callback() {
            @Override
            public void onSuccess() {
                picPB.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                picPB.setVisibility(View.VISIBLE);
                Toast.makeText(details.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
        title.setText(meal.getStrMeal());
        category.setText(meal.getStrCategory());
//        country.setText(meal.getStrArea());

        if(!(meal.getStrInstructions() == null))
            instructions.setText("\n" + meal.getStrInstructions() + "\n");

        if (!(meal.getStrIngredient1() == null) && !meal.getStrIngredient1().isEmpty() && !(meal.getStrMeasure1() == null) && !meal.getStrMeasure1().isEmpty() && !Character.isWhitespace(meal.getStrMeasure1().charAt(0)))
            ingredients.append("\n 1) " + meal.getStrMeasure1()  + meal.getStrIngredient1());

        if (!(meal.getStrIngredient2() == null) && !meal.getStrIngredient2().isEmpty() && !(meal.getStrMeasure2() == null) && !meal.getStrMeasure2().isEmpty() && !Character.isWhitespace(meal.getStrMeasure2().charAt(0)))
            ingredients.append("\n 2) " + meal.getStrMeasure2() + meal.getStrIngredient2());

        if (!(meal.getStrIngredient3() == null) && !meal.getStrIngredient3().isEmpty() && !(meal.getStrMeasure3() == null) && !meal.getStrMeasure3().isEmpty() && !Character.isWhitespace(meal.getStrMeasure3().charAt(0)))
            ingredients.append("\n 3) " + meal.getStrMeasure3() + " " + meal.getStrIngredient3());

        if (!(meal.getStrIngredient4() == null) && !meal.getStrIngredient4().isEmpty() && !(meal.getStrMeasure4() == null) && !meal.getStrMeasure4().isEmpty() && !Character.isWhitespace(meal.getStrMeasure4().charAt(0)))
            ingredients.append("\n 4) " + meal.getStrMeasure4() + " " + meal.getStrIngredient4());

        if (!(meal.getStrIngredient5() == null) && !meal.getStrIngredient5().isEmpty() && !(meal.getStrMeasure5() == null) && !meal.getStrMeasure5().isEmpty() && !Character.isWhitespace(meal.getStrMeasure5().charAt(0)))
            ingredients.append("\n 5) " + meal.getStrMeasure5() + " " + meal.getStrIngredient5());

        if (!(meal.getStrIngredient6() == null) && !meal.getStrIngredient6().isEmpty() && !(meal.getStrMeasure6() == null) && !meal.getStrMeasure6().isEmpty() && !Character.isWhitespace(meal.getStrMeasure6().charAt(0)))
            ingredients.append("\n 6) " + meal.getStrMeasure6() + " " + meal.getStrIngredient6());

        if (!(meal.getStrIngredient7() == null) && !meal.getStrIngredient7().isEmpty() && !(meal.getStrMeasure7() == null) && !meal.getStrMeasure7().isEmpty() && !Character.isWhitespace(meal.getStrMeasure7().charAt(0)))
            ingredients.append("\n 7) " + meal.getStrMeasure7() + " " + meal.getStrIngredient7());

        if (!(meal.getStrIngredient8() == null) && !meal.getStrIngredient8().isEmpty() && !(meal.getStrMeasure8() == null) && !meal.getStrMeasure8().isEmpty() && !Character.isWhitespace(meal.getStrMeasure8().charAt(0)))
            ingredients.append("\n 8) " + meal.getStrMeasure8() + " " + meal.getStrIngredient8());

        if (!(meal.getStrIngredient9() == null) && !meal.getStrIngredient9().isEmpty() && !(meal.getStrMeasure9() == null) && !meal.getStrMeasure9().isEmpty() && !Character.isWhitespace(meal.getStrMeasure9().charAt(0)))
            ingredients.append("\n 9) " + meal.getStrMeasure9() + " " + meal.getStrIngredient9());

        if (!(meal.getStrIngredient10() == null) && !meal.getStrIngredient10().isEmpty() && !(meal.getStrMeasure10() == null) && !meal.getStrMeasure10().isEmpty() && !Character.isWhitespace(meal.getStrMeasure10().charAt(0)))
            ingredients.append("\n 10) " + meal.getStrMeasure10() + " " + meal.getStrIngredient10());

        if (!(meal.getStrIngredient11() == null) && !meal.getStrIngredient11().isEmpty() && !(meal.getStrMeasure11() == null) && !meal.getStrMeasure11().isEmpty() && !Character.isWhitespace(meal.getStrMeasure11().charAt(0)))
            ingredients.append("\n 11) " + meal.getStrMeasure11() + " " + meal.getStrIngredient11());

        if (!(meal.getStrIngredient12() == null) && !meal.getStrIngredient12().isEmpty() && !(meal.getStrMeasure12() == null) && !meal.getStrMeasure12().isEmpty() && !Character.isWhitespace(meal.getStrMeasure12().charAt(0)))
            ingredients.append("\n 12) " + meal.getStrMeasure12() + " " + meal.getStrIngredient12());

        if (!(meal.getStrIngredient13() == null) && !meal.getStrIngredient13().isEmpty() && !(meal.getStrMeasure13() == null) && !meal.getStrMeasure13().isEmpty() && !Character.isWhitespace(meal.getStrMeasure13().charAt(0)))
            ingredients.append("\n 13) " + meal.getStrMeasure13() + " " + meal.getStrIngredient13());

        if (!(meal.getStrIngredient14() == null) && !meal.getStrIngredient14().isEmpty() && !(meal.getStrMeasure14() == null) && !meal.getStrMeasure14().isEmpty() && !Character.isWhitespace(meal.getStrMeasure14().charAt(0)))
            ingredients.append("\n 14) " + meal.getStrMeasure14() + " " + meal.getStrIngredient14());

        if (!(meal.getStrIngredient15() == null) && !meal.getStrIngredient15().isEmpty() && !(meal.getStrMeasure15() == null) && !meal.getStrMeasure15().isEmpty() && !Character.isWhitespace(meal.getStrMeasure15().charAt(0)))
            ingredients.append("\n 15) " + meal.getStrMeasure15() + " " + meal.getStrIngredient15());

        if (!(meal.getStrIngredient16() == null) && !meal.getStrIngredient16().isEmpty() && !(meal.getStrMeasure16() == null) && !meal.getStrMeasure16().isEmpty() && !Character.isWhitespace(meal.getStrMeasure16().charAt(0)))
            ingredients.append("\n 16) " + meal.getStrMeasure16() + " " + meal.getStrIngredient16());

        if (!(meal.getStrIngredient17() == null) && !meal.getStrIngredient17().isEmpty() && !(meal.getStrMeasure17() == null) && !meal.getStrMeasure17().isEmpty() && !Character.isWhitespace(meal.getStrMeasure17().charAt(0)))
            ingredients.append("\n 17) " + meal.getStrMeasure17() + " " + meal.getStrIngredient17());

        if (!(meal.getStrIngredient18() == null) && !meal.getStrIngredient18().isEmpty() && !(meal.getStrMeasure18() == null) && !meal.getStrMeasure18().isEmpty() && !Character.isWhitespace(meal.getStrMeasure18().charAt(0)))
            ingredients.append("\n 18) " + meal.getStrMeasure18() + " " + meal.getStrIngredient18());

        if (!(meal.getStrIngredient19() == null) && !meal.getStrIngredient19().isEmpty() && !(meal.getStrMeasure19() == null) && !meal.getStrMeasure19().isEmpty() && !Character.isWhitespace(meal.getStrMeasure19().charAt(0)))
            ingredients.append("\n 19) " + meal.getStrMeasure19() + " " + meal.getStrIngredient19());

        if (!(meal.getStrIngredient20() == null) && !meal.getStrIngredient20().isEmpty() && !(meal.getStrMeasure20() == null) && !meal.getStrMeasure20().isEmpty() && !Character.isWhitespace(meal.getStrMeasure20().charAt(0)))
            ingredients.append("\n 20) " + meal.getStrMeasure20() + " " + meal.getStrIngredient20());


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);

                Uri url = Uri.parse(meal.getStrYoutube());
                if(url == null) {

                    Toast.makeText(details.this, "This video was removed / Can't be found", Toast.LENGTH_SHORT).show();
                    return;
                }

                intent.setData(url);
                startActivity(intent);
            }
        });

        sourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(meal.getStrSource());

                if(uri == null) {
                    Toast.makeText(details.this, "This article was not found or was removed", Toast.LENGTH_SHORT).show();
                    return;
                }

                intent.setData(uri);
                startActivity(intent);

            }
        });

    }

    @Override
    public void ifExceptions(String msg) {

        misc.showDialogMessage(this, "Error!", msg);
    }
}