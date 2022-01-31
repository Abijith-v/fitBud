package com.example.fitnessapp.diets.category;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.fitnessapp.R;
import com.example.fitnessapp.diets.Categories;
import com.example.fitnessapp.diets.adapters.categoryViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.api.LogDescriptor;

import java.util.List;

public class categoryHomePage extends AppCompatActivity {

//    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPagerCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_home_page);

//        toolbar = findViewById(R.id.toolbarCategory);
        tabLayout = findViewById(R.id.tabLayoutCategory);
        viewPagerCategories = findViewById(R.id.ViewPagerCategory);

//        //set tool bar
//        setSupportActionBar(toolbar);
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle(null);
//        }

        // get intent
        Intent intent = getIntent();
        List<Categories.Category> categories = (List<Categories.Category>) intent.getSerializableExtra("categoryFromHealthFrag");
        int position = intent.getIntExtra("positionFromHealthFrag", 0);

        Log.d("categoryHomePage", "" + position);

        //set view pager
        categoryViewPager VPadapter = new categoryViewPager(getSupportFragmentManager(), categories);
        viewPagerCategories.setAdapter(VPadapter);

        //Setup the tab layout with the viewpager
        tabLayout.setupWithViewPager(viewPagerCategories);

        viewPagerCategories.setCurrentItem(position, true);
        VPadapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}