package com.example.chillody.Activity_Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chillody.Adapter.FavPageAdapter;
import com.example.chillody.Model.FavoriteYoutubeElement;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.example.chillody.Networking.YoutubeExecutor;
import com.example.chillody.R;
import com.example.chillody.databinding.HomeLayoutFragmentBinding;
import com.example.chillody.databinding.LovingPlaylistFragmentLayoutBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class loving_playlist_fragment extends Fragment {
    private LovingPlaylistFragmentLayoutBinding binding;
    private Player.Listener listener;
    private TextView titleTrackTextview;
    private boolean isHappenBefore = false;
    private SharedPreferences sharedPreferences;
    private SingletonExoPlayer singletonExoPlayer;
    private ImageView WhiteLoveBtn, RedLoveBtn;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(home_fragment.sharedFile,Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("QuocBug", "onCreateView: create the fragment");
        singletonExoPlayer = SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication());
        binding = LovingPlaylistFragmentLayoutBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.lovingplayliststyledPlayerControlView.setPlayer(singletonExoPlayer.getExoPlayer());
        titleTrackTextview = binding.lovingplayliststyledPlayerControlView.findViewById(R.id.titletrackID);
        WhiteLoveBtn = binding.lovingplayliststyledPlayerControlView.findViewById(R.id.heartbtnID);
        RedLoveBtn = binding.lovingplayliststyledPlayerControlView.findViewById(R.id.heartREDbtnID);

        // Processing the TabLayout
        binding.tablayoutID.addTab(binding.tablayoutID.newTab().setText("Favorite Songs"));
        binding.tablayoutID.addTab(binding.tablayoutID.newTab().setText("Favorite Images"));
        FavPageAdapter favPageAdapter = new FavPageAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),binding.tablayoutID.getTabCount());
        binding.viewpagerID.setAdapter(favPageAdapter);
        // There are 2 listeners for the Tab and its corresponding content layout
        binding.viewpagerID.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tablayoutID));
        binding.tablayoutID.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               binding.viewpagerID.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        WhiteLoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                if(item != null && item.localConfiguration != null){
                    YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                    WhiteLoveBtn.setVisibility(View.GONE);
                    RedLoveBtn.setVisibility(View.VISIBLE);
                    // I don't know why the YoutubeMusicElement in the tag is really the real instance ...
                    // this is so magic.. I imagine that it's just a hashed object.
                    element.setFavorite(true);
                    home_fragment.favoriteYoutubeViewModel.InsertFavoriteSongs(new FavoriteYoutubeElement(element.getMusicID(), element.getDownloadedMusicUrl(), element.getTitle(), singletonExoPlayer.getType()+"Love"));
                }
            }
        });
        RedLoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                WhiteLoveBtn.setVisibility(View.VISIBLE);
                RedLoveBtn.setVisibility(View.GONE);
                YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                element.setFavorite(false);
                home_fragment.favoriteYoutubeViewModel.DeleteSongElements(new FavoriteYoutubeElement(element.getMusicID(), element.getDownloadedMusicUrl(), element.getTitle(), singletonExoPlayer.getType()+"Love"));
            }
        });
        MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
        if(item != null && item.localConfiguration != null)
        {
            YoutubeMusicElement currentElement = (YoutubeMusicElement) item.localConfiguration.tag;
            titleTrackTextview.setText(currentElement.getTitle());
            if(currentElement.isFavorite()){
                WhiteLoveBtn.setVisibility(View.GONE );
                RedLoveBtn.setVisibility(View.VISIBLE);
            }
            // Not checking else condition here because by default, the RedHeardIcon is set to Gone.
        }
        // to load more song if the current song is gonna turn to the last song in the list
        // !singletonExoPlayer.isThreadProcessing() is to prevent the case in which
        // the first song is the current item and the second one is loading into the list
        // i.e this helps you to guarantee that there's only one process to load more recommendation songs.
        if(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() == singletonExoPlayer.getExoPlayer().getMediaItemCount()-1 && !singletonExoPlayer.isThreadProcessing()){
            if(singletonExoPlayer.getType().contains("Love")) return;
            Log.d("QuocMusic", "onPlaybackStateChanged: Loading more song");
            YoutubeMusicElement LastElement = (YoutubeMusicElement) Objects.requireNonNull(singletonExoPlayer.getExoPlayer().getCurrentMediaItem().localConfiguration).tag;
            new YoutubeExecutor(Objects.requireNonNull(getActivity()).getApplication()).MusicRecommendingExecutor(LastElement.getMusicID(),null,null);
        }

        // Processing the ExoPlayer


        listener = new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                // error occurs
                Throwable cause = error.getCause();
                if (cause instanceof HttpDataSource.HttpDataSourceException) {
                    HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                    if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                        Toast.makeText(binding.getRoot().getContext(), "There's a error! Please wait a minute", Toast.LENGTH_SHORT).show();
                        Log.d("QuocBug", "onPlayerError: the link is error");
                        singletonExoPlayer.EndMusic();
                        Toast.makeText(binding.getRoot().getContext(), "Error occurs", Toast.LENGTH_SHORT).show();
                        titleTrackTextview.setText(R.string.NoSong_Notification);
                    }
                }
            }
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                if(mediaItem != null && mediaItem.localConfiguration != null){
                    YoutubeMusicElement element = (YoutubeMusicElement) mediaItem.localConfiguration.tag;
                    titleTrackTextview.setText(element.getTitle());
                }
                if(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() == singletonExoPlayer.getExoPlayer().getMediaItemCount()-1){
                    if(singletonExoPlayer.getType().contains("Love")) return;
                    Log.d("QuocMusic", "onPlaybackStateChanged: Loading more song");
                    if(singletonExoPlayer.isThreadProcessing())
                        Log.d("QuocBug", "onMediaItemTransition: True");
                    else Log.d("QuocBug", "onMediaItemTransition: False");
                    YoutubeMusicElement LastElement = (YoutubeMusicElement) singletonExoPlayer.getExoPlayer().getCurrentMediaItem().localConfiguration.tag;
                    new YoutubeExecutor(Objects.requireNonNull(getActivity()).getApplication()).MusicRecommendingExecutor(LastElement.getMusicID(),null,null);
                }
            }
        };
        singletonExoPlayer.getExoPlayer().addListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        singletonExoPlayer.getExoPlayer().removeListener(listener);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // This is to save the last state of ExoPlayer instance
        Set<String> titleSet = new LinkedHashSet<>();
        Set<String> urlSet = new LinkedHashSet<>();
        Set<String> idSet = new LinkedHashSet<>();
        ExoPlayer exoPlayer = SingletonExoPlayer.getInstance(getActivity().getApplication()).getExoPlayer();
        String exoPlayerType = SingletonExoPlayer.getInstance(getActivity().getApplication()).getType();
        for(int i=0; i< exoPlayer.getMediaItemCount(); i++){
            YoutubeMusicElement element = (YoutubeMusicElement) Objects.requireNonNull(exoPlayer.getMediaItemAt(i).localConfiguration).tag;
            titleSet.add(element.getTitle());
            Log.d("QuocBug", "onPause: "+ element.getTitle());
            Log.d("QuocBug","onPause: + url: "+ element.getDownloadedMusicUrl());
            urlSet.add(element.getDownloadedMusicUrl());
            idSet.add(element.getMusicID());
        }
        int MediaItemPosition =exoPlayer.getCurrentMediaItemIndex();
        editor.putInt(home_fragment.LAST_EXOPOSITEM_STATE,MediaItemPosition);
        Log.d("QuocBug", "onPause: MediaPosition: "+String.valueOf(MediaItemPosition));
        editor.putString(home_fragment.LAST_EXOID_STATE,new JSONArray(idSet).toString());
        editor.putString(home_fragment.LAST_EXOTITLE_STATE,new JSONArray(titleSet).toString());
        editor.putString(home_fragment.LAST_EXOURL_STATE,new JSONArray(urlSet).toString());
        editor.putString(home_fragment.LAST_EXOTYPE_STATE,exoPlayerType);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isHappenBefore){
            MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
            // This is to update the UI, sync the data of ExoPlayer of category fragment into this fragment
            if(item != null && item.localConfiguration != null){
                YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                titleTrackTextview.setText(element.getTitle());
            }
            if(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() == singletonExoPlayer.getExoPlayer().getMediaItemCount()-1 && !singletonExoPlayer.isThreadProcessing()){
                if(singletonExoPlayer.getType().contains("Love")) return;
                Log.d("QuocMusic", "onPlaybackStateChanged: Loading more song");
                YoutubeMusicElement LastElement = (YoutubeMusicElement) Objects.requireNonNull(singletonExoPlayer.getExoPlayer().getCurrentMediaItem().localConfiguration).tag;
                new YoutubeExecutor(Objects.requireNonNull(getActivity()).getApplication()).MusicRecommendingExecutor(LastElement.getMusicID(),null,null);
            }
            singletonExoPlayer.getExoPlayer().addListener(listener);
        }
        isHappenBefore = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("QuocTest", "onDestroy: onplaylist");
    }
}