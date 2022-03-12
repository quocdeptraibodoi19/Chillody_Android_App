package com.example.chillody.Adapter;

import android.media.Image;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.chillody.Activity_Fragment.Fav_Image_fragment;
import com.example.chillody.Activity_Fragment.Fav_Music_fragment;

public class FavPageAdapter extends FragmentStatePagerAdapter {
    private final int count;
    private TextView NameSong;
    public FavPageAdapter(@NonNull FragmentManager fm, int behavior,TextView nameSong) {
        super(fm, behavior);
        count = behavior;
        this.NameSong = nameSong;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) return new Fav_Music_fragment(NameSong);
        else if(position == 1) return new Fav_Image_fragment();
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }
}
