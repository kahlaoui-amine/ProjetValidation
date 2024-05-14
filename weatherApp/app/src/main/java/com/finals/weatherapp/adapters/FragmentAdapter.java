package com.finals.weatherapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.finals.weatherapp.fragments.WeatherCurrent;
import com.finals.weatherapp.fragments.WeatherForecast;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new WeatherForecast();
        }
        return new WeatherCurrent();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
