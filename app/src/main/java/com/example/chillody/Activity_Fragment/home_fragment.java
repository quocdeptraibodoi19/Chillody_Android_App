package com.example.chillody.Activity_Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chillody.Adapter.CategoryAdapter;
import com.example.chillody.Model.categoryObj;
import com.example.chillody.databinding.HomeLayoutFragmentBinding;

import java.util.ArrayList;
import java.util.List;

public class home_fragment extends Fragment {
    private List<categoryObj> categoryObjList = new ArrayList<>();
    private HomeLayoutFragmentBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryObjList.add(new categoryObj("Chilling","https://c0.wallpaperflare.com/preview/259/493/306/person-car-cloud-sunset.jpg"));
        categoryObjList.add(new categoryObj("Cafe","https://c4.wallpaperflare.com/wallpaper/805/668/874/lofi-neon-coffee-house-shop-neon-glow-hd-wallpaper-preview.jpg"));
        categoryObjList.add(new categoryObj("Ghibli","https://studioghiblimovies.com/wp-content/uploads/2020/03/barcode-scanners-qr-code-2d-code-creative-barcode.jpg"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =HomeLayoutFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CategoryAdapter categoryAdapter = new CategoryAdapter(binding.getRoot().getContext(),home_fragment.this);
        categoryAdapter.setCategoryObjList(categoryObjList);
        binding.recyclerView.setAdapter(categoryAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        //new MusicQuoteAsyncTask(binding.MusicQuoteID,binding.ArtistID,binding.getRoot().getContext()).execute();
    }
}