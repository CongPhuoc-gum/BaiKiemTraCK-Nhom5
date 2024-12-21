package com.example.baikiemtrack;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import fragment.fragment_home;
import fragment.fragment_profile;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new fragment_home();
            case 1:
                return new fragment_profile();

            default:
                return new fragment_home();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
