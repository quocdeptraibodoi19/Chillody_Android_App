package com.example.chillody.Activity_Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chillody.Adapter.CategoryAdapter;
import com.example.chillody.Model.FavoriteYoutubeElement;
import com.example.chillody.Model.FavoriteYoutubeViewModel;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.example.chillody.Model.categoryObj;
import com.example.chillody.Networking.YoutubeExecutor;
import com.example.chillody.R;
import com.example.chillody.databinding.HomeLayoutFragmentBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.upstream.HttpDataSource;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
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
    private String exoPlayerType;
    private int MediaItemPosition;
    private boolean isHappenBefore = false;
    private ImageView RedHeartIcon,WhiteHeartIcon;
    public static FavoriteYoutubeViewModel favoriteYoutubeViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("QuocLife", "HomeFragment: onCreate: ");
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(sharedFile,Context.MODE_PRIVATE);
        categoryObjList.add(new categoryObj("Chilling","https://c0.wallpaperflare.com/preview/259/493/306/person-car-cloud-sunset.jpg"));
        categoryObjList.add(new categoryObj("Cafe","https://c4.wallpaperflare.com/wallpaper/805/668/874/lofi-neon-coffee-house-shop-neon-glow-hd-wallpaper-preview.jpg"));
        categoryObjList.add(new categoryObj("Ghibli","https://studioghiblimovies.com/wp-content/uploads/2020/03/barcode-scanners-qr-code-2d-code-creative-barcode.jpg"));
        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication());
        // This is to load data from the Shared Reference into the ExoPlayer instance:
        // the if is to prevent the case when the configuration changes occur, the home_fragment recreate and it will automatically add data to the
        // the current singleton exo ( which is still alive in spite of configuration changes)
        if(singletonExoPlayer.getExoPlayer().getMediaItemCount() == 0)
        {
            Log.d("QuocBug", "onCreate: inside the home_fragment ");
            JSONArray titleSet = null;
            JSONArray urlSet = null;
            JSONArray idSet = null;
            try {
                titleSet = new JSONArray(sharedPreferences.getString(LAST_EXOTITLE_STATE,"[]"));
                urlSet = new JSONArray(sharedPreferences.getString(LAST_EXOURL_STATE,"[]"));
                idSet = new JSONArray(sharedPreferences.getString(LAST_EXOID_STATE,"[]"));
                int length = Math.min(titleSet.length(),Math.min(urlSet.length(),idSet.length()));
                exoPlayerType = sharedPreferences.getString(LAST_EXOTYPE_STATE,"");
                MediaItemPosition = sharedPreferences.getInt(LAST_EXOPOSITEM_STATE,0);
                Log.d("QuocBug", "onCreate: The position is: "+ String.valueOf(MediaItemPosition));
                singletonExoPlayer.setType(exoPlayerType);
                Log.d("QuocBug", "onCreate: size of array"+String.valueOf(titleSet.length()));
                for(int i=0; i<length;i++){
                    if(titleSet.getString(i).equals("") || urlSet.getString(i).equals("") || idSet.getString(i).equals(""))
                    {
                        if(i == MediaItemPosition) MediaItemPosition = 0;
                        continue;
                    }
                    Log.d("QuocBug", "onCreate: The title: "+titleSet.getString(i));
                    Log.d("QuocBug", "onCreate: the url: "+ urlSet.getString(i));
                    MediaItem mediaItem = new MediaItem.Builder()
                            .setUri(urlSet.getString(i)).setTag(new YoutubeMusicElement(titleSet.getString(i), idSet.getString(i), urlSet.getString(i)))
                            .build();
                    singletonExoPlayer.getExoPlayer().addMediaItem(mediaItem);
                    if(MediaItemPosition == i) {
                        MediaItemPosition = singletonExoPlayer.getExoPlayer().getMediaItemCount()-1;
                    }
                }
                if(singletonExoPlayer.getExoPlayer().getMediaItemCount() !=0)
                {

                    Log.d("QuocBug", "onCreate: MediaItemPosition");
                    singletonExoPlayer.getExoPlayer().seekTo(MediaItemPosition,0);
                    singletonExoPlayer.getExoPlayer().prepare();
                    singletonExoPlayer.getExoPlayer().play();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("QuocLife", "HomeFragment: onCreateView: ");
        // Inflate the layout for this fragment
        binding =HomeLayoutFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("QuocLife", "HomeFragment: onViewCreated: ");
        // Move to another fragment:
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(home_fragment.this).navigate(R.id.action_home_fragment_to_loving_playlist_fragment);
            }
        });

        CategoryAdapter categoryAdapter = new CategoryAdapter(binding.getRoot().getContext(),home_fragment.this);
        categoryAdapter.setCategoryObjList(categoryObjList);
        binding.recyclerView.setAdapter(categoryAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        //new MusicQuoteAsyncTask(binding.MusicQuoteID,binding.ArtistID,binding.getRoot().getContext()).execute();
        titleTrackTextview = binding.styledPlayerControlView.findViewById(R.id.titletrackID);
        RedHeartIcon = binding.styledPlayerControlView.findViewById(R.id.heartREDbtnID);
        WhiteHeartIcon = binding.styledPlayerControlView.findViewById(R.id.heartbtnID);

        favoriteYoutubeViewModel = ViewModelProviders.of(this).get(FavoriteYoutubeViewModel.class);

        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication());
        binding.styledPlayerControlView.setPlayer(singletonExoPlayer.getExoPlayer());
        Log.d("QuocBug", "onViewCreated: in the home_fragment of the onviewcreated");
        MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
        // This is to update the UI, sync the data of ExoPlayer of category fragment into this fragment
        if(item != null && item.localConfiguration != null ){
            YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
            titleTrackTextview.setText(element.getTitle());
            if(element.isFavorite()){
                RedHeartIcon.setVisibility(View.VISIBLE);
                WhiteHeartIcon.setVisibility(View.GONE);
            }
            // Not checking else condition here because by default, the RedHeardIcon is set to Gone.
        }
        if(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() == singletonExoPlayer.getExoPlayer().getMediaItemCount()-1 && !singletonExoPlayer.isThreadProcessing()){
            if(singletonExoPlayer.getType().contains("Love")) return;
            Log.d("QuocMusic", "onPlaybackStateChanged: Loading more song");
            YoutubeMusicElement LastElement = (YoutubeMusicElement) singletonExoPlayer.getExoPlayer().getCurrentMediaItem().localConfiguration.tag;
            new YoutubeExecutor(getActivity().getApplication()).MusicRecommendingExecutor(LastElement.getMusicID(),null,null);
        }
        WhiteHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                if(item != null && item.localConfiguration != null){
                    YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                    RedHeartIcon.setVisibility(View.VISIBLE);
                    WhiteHeartIcon.setVisibility(View.GONE);
                    // I don't know why the YoutubeMusicElement in the tag is really the real instance ...
                    // this is so magic.. I thought that it's just a hashed object.
                    element.setFavorite(true);
                    Log.d("QuocBug", "onClick: whiteheart click: "+element.getTitle());
                    favoriteYoutubeViewModel.InsertFavoriteSongs(new FavoriteYoutubeElement(element.getMusicID(), element.getDownloadedMusicUrl(), element.getTitle(), singletonExoPlayer.getType()+"Love"));
                }
            }
        });
        RedHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteHeartIcon.setVisibility(View.VISIBLE);
                RedHeartIcon.setVisibility(View.GONE);
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                // we dont need to check if the item here is null because if it's null then
                // the White Button can't be clicked. Hence, the red one can not appear
                // due to the default layout.
                YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                element.setFavorite(false);
                favoriteYoutubeViewModel.DeleteSongElements(new FavoriteYoutubeElement(element.getMusicID(), element.getDownloadedMusicUrl(), element.getTitle(), singletonExoPlayer.getType()+"Love"));
            }
        });
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
                        RedHeartIcon.setVisibility(View.GONE);
                        WhiteHeartIcon.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                if(mediaItem != null && mediaItem.localConfiguration != null){
                    YoutubeMusicElement element = (YoutubeMusicElement) mediaItem.localConfiguration.tag;
                    titleTrackTextview.setText(element.getTitle());
                    if(element.isFavorite()){
                        RedHeartIcon.setVisibility(View.VISIBLE);
                        WhiteHeartIcon.setVisibility(View.GONE);
                    }
                    else{
                        RedHeartIcon.setVisibility(View.GONE);
                        WhiteHeartIcon.setVisibility(View.VISIBLE);
                    }
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
        Log.d("QuocLife", "HomeFragment: onPause: ");
        SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication()).getExoPlayer().removeListener(listener);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // This is to save the last state of ExoPlayer instance
        Set<String> titleSet = new LinkedHashSet<>();
        Set<String> urlSet = new LinkedHashSet<>();
        Set<String> idSet = new LinkedHashSet<>();
       ExoPlayer exoPlayer = SingletonExoPlayer.getInstance(getActivity().getApplication()).getExoPlayer();
       exoPlayerType = SingletonExoPlayer.getInstance(getActivity().getApplication()).getType();
       for(int i=0; i< exoPlayer.getMediaItemCount(); i++){
           YoutubeMusicElement element = (YoutubeMusicElement) Objects.requireNonNull(exoPlayer.getMediaItemAt(i).localConfiguration).tag;
            titleSet.add(element.getTitle());
           Log.d("QuocBug", "onPause: "+ element.getTitle());
           Log.d("QuocBug","onPause: + url: "+ element.getDownloadedMusicUrl());
           urlSet.add(element.getDownloadedMusicUrl());
            idSet.add(element.getMusicID());
       }
       MediaItemPosition =exoPlayer.getCurrentMediaItemIndex();
       editor.putInt(LAST_EXOPOSITEM_STATE,MediaItemPosition);
        Log.d("QuocBug", "onPause: MediaPosition: "+String.valueOf(MediaItemPosition));
       editor.putString(LAST_EXOID_STATE,new JSONArray(idSet).toString());
       editor.putString(LAST_EXOTITLE_STATE,new JSONArray(titleSet).toString());
       editor.putString(LAST_EXOURL_STATE,new JSONArray(urlSet).toString());
       editor.putString(LAST_EXOTYPE_STATE,exoPlayerType);
       editor.apply();
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("QuocLife", "HomeFragment: onResume: ");
        if(isHappenBefore){
              SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication());
              binding.styledPlayerControlView.setPlayer(singletonExoPlayer.getExoPlayer());
              MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
              if(item != null && item.localConfiguration != null)
              {
                  YoutubeMusicElement currentElement = (YoutubeMusicElement) item.localConfiguration.tag;
                  titleTrackTextview.setText(currentElement.getTitle());
                  if(currentElement.isFavorite()){
                      RedHeartIcon.setVisibility(View.VISIBLE);
                      WhiteHeartIcon.setVisibility(View.GONE);
                  }
                  else{
                      RedHeartIcon.setVisibility(View.GONE);
                      WhiteHeartIcon.setVisibility(View.VISIBLE);
                  }
              }
             // This is to update the UI, sync the data of ExoPlayer of category fragment into this fragment
            // remember that this has singleton.isprocesisng() that i do not know the purpose
            if(item != null && item.localConfiguration != null){
                YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                titleTrackTextview.setText(element.getTitle());
            }
            if(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() == singletonExoPlayer.getExoPlayer().getMediaItemCount()-1 && !singletonExoPlayer.isThreadProcessing()){
                if(singletonExoPlayer.getType().contains("Love")) return;
                Log.d("QuocMusic", "onPlaybackStateChanged: Loading more song");
                YoutubeMusicElement LastElement = (YoutubeMusicElement) Objects.requireNonNull(singletonExoPlayer.getExoPlayer().getCurrentMediaItem().localConfiguration).tag;
                new YoutubeExecutor(getActivity().getApplication()).MusicRecommendingExecutor(LastElement.getMusicID(),null,null);
            }
            singletonExoPlayer.getExoPlayer().addListener(listener);
        }
        isHappenBefore = true;
    }

}