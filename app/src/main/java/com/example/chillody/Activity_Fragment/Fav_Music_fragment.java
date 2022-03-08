package com.example.chillody.Activity_Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chillody.Adapter.FavSongAdapter;
import com.example.chillody.Model.FavoriteYoutubeElement;
import com.example.chillody.Model.FavoriteYoutubeViewModel;
import com.example.chillody.Model.GeneralYoutubeViewModel;
import com.example.chillody.databinding.FavMusicLayoutBinding;

import java.util.List;
import java.util.Objects;


public class Fav_Music_fragment extends Fragment {
    private FavMusicLayoutBinding binding;
    private FavSongAdapter ChillingAdapter,CafeAdapter,GhibliAdapter;
    private FavoriteYoutubeViewModel favoriteYoutubeViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FavMusicLayoutBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favoriteYoutubeViewModel = ViewModelProviders.of(this).get(FavoriteYoutubeViewModel.class);
        Log.d("QuocMusicFragment", "onViewCreated: Creating");
        ChillingAdapter = new FavSongAdapter(binding.getRoot().getContext(), Objects.requireNonNull(getActivity()).getApplication(), favoriteYoutubeViewModel);
        binding.ChillingfavmusiclistID.setAdapter(ChillingAdapter);
        binding.ChillingfavmusiclistID.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        CafeAdapter = new FavSongAdapter(binding.getRoot().getContext(),getActivity().getApplication(), favoriteYoutubeViewModel);
        binding.CafefavmusiclistID.setAdapter(CafeAdapter);
        binding.CafefavmusiclistID.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        GhibliAdapter = new FavSongAdapter(binding.getRoot().getContext(),getActivity().getApplication(), favoriteYoutubeViewModel);
        binding.GhiblifavmusiclistID.setAdapter(GhibliAdapter);
        binding.GhiblifavmusiclistID.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        // fetching data into the adapters
        favoriteYoutubeViewModel.getFavChillingSongs().observe(getViewLifecycleOwner(), new Observer<List<FavoriteYoutubeElement>>() {
            @Override
            public void onChanged(List<FavoriteYoutubeElement> favoriteYoutubeElements) {
                if(favoriteYoutubeElements.size()==0) binding.ChillingmusictextviewID.setVisibility(View.GONE);
                else binding.ChillingmusictextviewID.setVisibility(View.VISIBLE);
                Log.d("QuocLiveChange", "onChanged: chillingnumber: "+String.valueOf(favoriteYoutubeElements.size()));
                ChillingAdapter.setCurrentList(favoriteYoutubeElements);
            }
        });
        favoriteYoutubeViewModel.getFavCafeSongs().observe(getViewLifecycleOwner(), new Observer<List<FavoriteYoutubeElement>>() {
            @Override
            public void onChanged(List<FavoriteYoutubeElement> favoriteYoutubeElements) {
                if(favoriteYoutubeElements.size() ==0) binding.CafemusictextviewID.setVisibility(View.GONE);
                else binding.CafemusictextviewID.setVisibility(View.VISIBLE);
                Log.d("QuocLiveChange", "onChanged: cafenumber: "+String.valueOf(favoriteYoutubeElements.size()));
                CafeAdapter.setCurrentList(favoriteYoutubeElements);
            }
        });
        favoriteYoutubeViewModel.getFavGhibliSongs().observe(getViewLifecycleOwner(), new Observer<List<FavoriteYoutubeElement>>() {
            @Override
            public void onChanged(List<FavoriteYoutubeElement> favoriteYoutubeElements) {
                if(favoriteYoutubeElements.size() == 0) binding.GhiblimusictextviewID.setVisibility(View.GONE);
                else binding.GhiblimusictextviewID.setVisibility(View.VISIBLE);
                Log.d("QuocLiveChange", "onChanged: ghiblinumber: "+String.valueOf(favoriteYoutubeElements.size()));
                GhibliAdapter.setCurrentList(favoriteYoutubeElements);
            }
        });
    }
    // why the onDestroy is not called here ... it because this in fact not a real fragment
    // this just a fragment living in the adapter ... therefore, when it's canceled out... it will not invoke
    // the onDestroy() ... Why it leads to this still remains mysterious.

}