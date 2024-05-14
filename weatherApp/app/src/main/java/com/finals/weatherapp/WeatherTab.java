package com.finals.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.finals.weatherapp.adapters.FragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class WeatherTab extends AppCompatActivity {
    ImageView ivBack;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    FragmentAdapter fragmentAdapter;
    FragmentManager fragmentManager;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_tab);

        ivBack = findViewById(R.id.ivBack);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Creates a two-tab fragment
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Current Weather"));
        tabLayout.addTab(tabLayout.newTab().setText("8-Day Forecast"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Allows swiping between the two tabs
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        // ivBack onClickListener
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSharedPreferences();
                finish();
            }
        });
    }

    // Clears SharedPreferences
    protected void clearSharedPreferences() {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("longitude");
        editor.remove("latitude");
        editor.apply();
    }
}