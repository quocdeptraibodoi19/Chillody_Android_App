package com.example.chillody.Activity_Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chillody.Adapter.CategoryAdapter;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.example.chillody.Model.categoryObj;
import com.example.chillody.Networking.YoutubeExecutor;
import com.example.chillody.R;
import com.example.chillody.databinding.HomeLayoutFragmentBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class home_fragment extends Fragment {
    public final static String sharedFile ="com.example.chillody.Activity_Fragment";
    public final static String LAST_EXOTYPE_STATE = "NDQEXOPLAYERTYPE";
    public final static String LAST_EXOID_STATE = "NDQEXOPLAYERIDSTATE";
    public final static String LAST_EXOURL_STATE = "NDQEXOPLAYERURLSTATE";
    public final static String LAST_EXOTITLE_STATE = "NDQEXOPLAYERTITLESTATE";
    public final static String LAST_EXOPOSITEM_STATE = "NDQEXOPLAYERITEMPOSITIONSTATE";
    private SharedPreferences sharedPreferences;
    private final List<categoryObj> categoryObjList = new ArrayList<>();
    private HomeLayoutFragmentBinding binding;
    private TextView titleTrackTextview;
    private Player.Listener listener;
    private Set<String> titleSet ;
    private Set<String> urlSet ;
    private Set<String> idSet ;
    private String exoPlayerType;
    private int MediaItemPosition;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences(sharedFile,Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryObjList.add(new categoryObj("Chilling","https://c0.wallpaperflare.com/preview/259/493/306/person-car-cloud-sunset.jpg"));
        categoryObjList.add(new categoryObj("Cafe","https://c4.wallpaperflare.com/wallpaper/805/668/874/lofi-neon-coffee-house-shop-neon-glow-hd-wallpaper-preview.jpg"));
        categoryObjList.add(new categoryObj("Ghibli","https://studioghiblimovies.com/wp-content/uploads/2020/03/barcode-scanners-qr-code-2d-code-creative-barcode.jpg"));
        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication());
        // This is to load data from the Shared Reference into the ExoPlayer instance:
        // the if is to prevent the case when the configuration changes occur, the home_fragment recreate and it will automatically add data to the
        // the current singleton exo ( which is still alive in spite of configuration changes)
        if(singletonExoPlayer.getExoPlayer().getMediaItemCount() == 0)
        {
            titleSet = sharedPreferences.getStringSet(LAST_EXOTITLE_STATE,new LinkedHashSet<>());
            urlSet = sharedPreferences.getStringSet(LAST_EXOURL_STATE,new LinkedHashSet<>());
            idSet = sharedPreferences.getStringSet(LAST_EXOID_STATE,new LinkedHashSet<>());
            exoPlayerType = sharedPreferences.getString(LAST_EXOTYPE_STATE,"");
            MediaItemPosition = sharedPreferences.getInt(LAST_EXOPOSITEM_STATE,0);
            String[] titleStrings = new String[titleSet.size()];
            String[] urlStrings = new String[urlSet.size()];
            String[] idStrings = new String[idSet.size()];
            titleStrings = titleSet.toArray(titleStrings);
            urlStrings = urlSet.toArray(urlStrings);
            idStrings = idSet.toArray(idStrings);
            singletonExoPlayer.setType(exoPlayerType);
            for(int i=0; i<titleSet.size();i++){
                MediaItem mediaItem = new MediaItem.Builder()
                        .setUri(urlStrings[i]).setTag(new YoutubeMusicElement(titleStrings[i],idStrings[i],urlStrings[i]))
                        .build();
                singletonExoPlayer.getExoPlayer().addMediaItem(mediaItem);
            }
            singletonExoPlayer.getExoPlayer().seekTo(MediaItemPosition,0);
            singletonExoPlayer.getExoPlayer().prepare();
            singletonExoPlayer.getExoPlayer().play();
        }
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
        titleTrackTextview = binding.styledPlayerControlView.findViewById(R.id.titletrackID);
        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication());
        binding.styledPlayerControlView.setPlayer(singletonExoPlayer.getExoPlayer());
        Log.d("QuocBug", "onViewCreated: in the home_fragment of the onviewcreated");
        MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
        // This is to update the UI, sync the data of ExoPlayer of category fragment into this fragment
        if(item != null && item.localConfiguration != null){
            YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
            titleTrackTextview.setText(element.getTitle());
        }
        if(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() == singletonExoPlayer.getExoPlayer().getMediaItemCount()-1){
            Log.d("QuocMusic", "onPlaybackStateChanged: Loading more song");
            new YoutubeExecutor(getActivity().getApplication()).MusicRecommendingExecutor(null,null);
        }
        listener = new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                if(mediaItem != null && mediaItem.localConfiguration != null){
                    YoutubeMusicElement element = (YoutubeMusicElement) mediaItem.localConfiguration.tag;
                    titleTrackTextview.setText(element.getTitle());
                }
                if(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() == singletonExoPlayer.getExoPlayer().getMediaItemCount()-1){
                    Log.d("QuocMusic", "onPlaybackStateChanged: Loading more song");
                    new YoutubeExecutor(getActivity().getApplication()).MusicRecommendingExecutor(null,null);
                }
            }
        };
        singletonExoPlayer.getExoPlayer().addListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication()).getExoPlayer().removeListener(listener);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // This is to save the last state of ExoPlayer instance
        titleSet = new LinkedHashSet<>();
        urlSet = new LinkedHashSet<>();
        idSet = new LinkedHashSet<>();
       ExoPlayer exoPlayer = SingletonExoPlayer.getInstance(getActivity().getApplication()).getExoPlayer();
       exoPlayerType = SingletonExoPlayer.getInstance(getActivity().getApplication()).getType();
       for(int i=0; i< exoPlayer.getMediaItemCount(); i++){
           Log.d("QuocBug", "onPause: ");
           YoutubeMusicElement element = (YoutubeMusicElement) Objects.requireNonNull(exoPlayer.getMediaItemAt(i).localConfiguration).tag;
            titleSet.add(element.getTitle());
            urlSet.add(element.getDownloadedMusicUrl());
            idSet.add(element.getMusicID());
       }
       MediaItemPosition =exoPlayer.getCurrentMediaItemIndex();
       editor.putInt(LAST_EXOPOSITEM_STATE,MediaItemPosition);
       editor.putStringSet(LAST_EXOID_STATE,idSet);
       editor.putStringSet(LAST_EXOTITLE_STATE,titleSet);
       editor.putStringSet(LAST_EXOURL_STATE,urlSet);
       editor.putString(LAST_EXOTYPE_STATE,exoPlayerType);
       editor.apply();
    }

}