package com.example.chillody.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.chillody.Activity_Fragment.Fav_Image_fragment;
import com.example.chillody.Activity_Fragment.Fav_Music_fragment;

public class FavPageAdapter extends FragmentStatePagerAdapter {
    private final int count;
    public FavPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        count = behavior;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) return new Fav_Music_fragment();
        else if(position == 1) return new Fav_Image_fragment();
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }
}
