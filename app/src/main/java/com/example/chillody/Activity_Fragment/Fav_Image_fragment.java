package com.example.chillody.Activity_Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chillody.Adapter.FavUnsplashImgAdapter;
import com.example.chillody.Model.FavoriteUnsplashImgViewModel;
import com.example.chillody.Model.UnsplashImgElement;
import com.example.chillody.R;
import com.example.chillody.databinding.FavImageLayoutBinding;

import java.util.List;

public class Fav_Image_fragment extends Fragment {
    private FavImageLayoutBinding binding;
    private FavUnsplashImgAdapter unsplashImgAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FavImageLayoutBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FavoriteUnsplashImgViewModel favoriteUnsplashImgViewModel = ViewModelProviders.of(this).get(FavoriteUnsplashImgViewModel.class);
        favoriteUnsplashImgViewModel.getFavoriteElementList().observe(getViewLifecycleOwner(), new Observer<List<UnsplashImgElement>>() {
            @Override
            public void onChanged(List<UnsplashImgElement> unsplashImgElements) {
                unsplashImgAdapter = new FavUnsplashImgAdapter(binding.getRoot().getContext(),R.layout.fav_image_item,unsplashImgElements);
                binding.UnsplashImgGridViewId.setAdapter(unsplashImgAdapter);
            }
        });
    }
}