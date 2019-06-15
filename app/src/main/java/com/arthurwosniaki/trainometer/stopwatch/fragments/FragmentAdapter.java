package com.arthurwosniaki.trainometer.stopwatch.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.arthurwosniaki.trainometer.R;

public class FragmentAdapter extends FragmentPagerAdapter {

    private Context context;
    private Bundle bundle;


    public FragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    public FragmentAdapter(Context context, FragmentManager fm, Bundle bundle) {
        super(fm);

        this.context = context;
        this.bundle = bundle;
    }

    //Determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            ChronometerFragment chronometerFragment = new ChronometerFragment();

            chronometerFragment.setArguments(bundle);
            return chronometerFragment;

        } else if (position == 1){
            TimerFragment timerFragment = new TimerFragment();

            timerFragment.setArguments(bundle);
            return timerFragment;
        }

        return null;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

        SharedPreferences sharedPref = context.getSharedPreferences("timer_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //Get the tags set by FragmentPagerAdapter and add to SharedPreferences;
        switch (position) {
            case 0:
                String chronometerTag = createdFragment.getTag();
                editor.putString("chronometer_id", chronometerTag);
                editor.apply();

                break;
            case 1:
                String timerTag = createdFragment.getTag();
                editor.putString("timer_id", timerTag);
                editor.apply();

                break;
        }
        return createdFragment;
    }

    // Determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // Determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return context.getString(R.string.chronometer);
            case 1:
                return context.getString(R.string.timer);
            default:
                return null;
        }
    }
}