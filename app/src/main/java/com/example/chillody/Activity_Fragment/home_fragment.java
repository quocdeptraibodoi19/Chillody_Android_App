package com.example.chillody.Activity_Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chillody.Adapter.CategoryAdapter;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.categoryObj;
import com.example.chillody.R;
import com.example.chillody.databinding.HomeLayoutFragmentBinding;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class home_fragment extends Fragment {
    private List<categoryObj> categoryObjList = new ArrayList<>();
    private HomeLayoutFragmentBinding binding;
    private TextView titleTrackTextview;
    private Player.Listener listener;
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
        //new MusicQuoteRunnable(binding.MusicQuoteID,binding.ArtistID,binding.getRoot().getContext()).execute();
        titleTrackTextview = binding.styledPlayerControlView.findViewById(R.id.titletrackID);
        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication());
        MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
        if(item != null && item.localConfiguration != null){
            String title = (String) item.localConfiguration.tag;
            titleTrackTextview.setText(title);
        }
        binding.styledPlayerControlView.setPlayer(singletonExoPlayer.getExoPlayer());
        listener = new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                if(mediaItem != null && mediaItem.localConfiguration != null){
                    String title = (String) mediaItem.localConfiguration.tag;
                    titleTrackTextview.setText(title);
                }
            }
        };
        singletonExoPlayer.getExoPlayer().addListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication()).getExoPlayer().removeListener(listener);
    }
}