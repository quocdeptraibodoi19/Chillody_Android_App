package com.example.chillody.Activity_Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chillody.Adapter.UnsplashImgAdapter;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.example.chillody.Model.YoutubeMusicModel;
import com.example.chillody.Networking.UnsplashAsynctask;
import com.example.chillody.Model.UnsplashImgModel;
import com.example.chillody.Networking.YoutubeExecutor;
import com.example.chillody.R;
import com.example.chillody.databinding.MusicLayoutFragmentBinding;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.squareup.moshi.Json;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class music_fragment extends Fragment {
    private MusicLayoutFragmentBinding binding;
    private UnsplashImgModel unsplashImgModel;
    private YoutubeMusicModel youtubeMusicModel;
    private YoutubeExecutor youtubeExecutor;
    private String page;
    private String ImgQuery;
    private String MusicQuery;
    private String nameOfCategory;
    private TextView CurrentSongNameTextView, NextSongNameTextView;
    private final static String PAGE_MESSAGE = "Here is the page message";
    private final static String IMAGE_QUERY_MESSAGE = "Here is the image query message";
    private Player.Listener listener;
    private SharedPreferences sharedPreferences;
    private int MediaItemPosition;
    private boolean isHappenBefore = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        youtubeMusicModel = ViewModelProviders.of(this).get(YoutubeMusicModel.class);
        nameOfCategory = getArguments().getString("NameCategory");
        page = "1";
        switch (nameOfCategory){
            case "Chilling":
                ImgQuery = "sadness city";
                MusicQuery = "Đến Cuối Cùng Lại Cô Đơn - Dickson";
                break;
            case "Ghibli":
                ImgQuery = "kyoto";
                MusicQuery = "Studio Ghibli Emotional Melody : Cello Collection with Calcifer[作業用、睡眠用BGM、ジブリのチェロメドレー、吉卜力大提琴音樂集]";
                break;
            case "Cafe":
                ImgQuery = "cozy cafe";
                MusicQuery = "japanese night cafe vibes / a lofi hip hop mix ~ chill with taiki";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nameOfCategory);
        }
        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(requireActivity().getApplication());
        sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(home_fragment.sharedFile+nameOfCategory, Context.MODE_PRIVATE);
        // pass data into the from the exoplayer into the view
        // save the data and maintain the exoplayer
        if(youtubeMusicModel.getLengthYoutubeList() == 0)
        {
            if(singletonExoPlayer.getType().equals(nameOfCategory)){
                Log.d("QuocBug", "onViewCreated: same type");
                YoutubeMusicElement currentElement;
                for(int i=0; i<singletonExoPlayer.getExoPlayer().getMediaItemCount();i++)
                    {
                        currentElement = (YoutubeMusicElement) Objects.requireNonNull(singletonExoPlayer.getExoPlayer().getMediaItemAt(i).localConfiguration).tag;
                        youtubeMusicModel.AddMusicElement(currentElement);
                    }
                youtubeMusicModel.setLastUpdateIndex(youtubeMusicModel.getLastUpdateIndex() + singletonExoPlayer.getExoPlayer().getMediaItemCount());
            }
            else{
                Log.d("QuocBug", "onViewCreated: NOT same type");
                singletonExoPlayer.EndMusic();
                singletonExoPlayer.setType(nameOfCategory);
                JSONArray titleArray = null;
                JSONArray urlArray = null;
                JSONArray idArray = null;
                try {
                    titleArray = new JSONArray(sharedPreferences.getString(home_fragment.LAST_EXOTITLE_STATE,"[]"));
                    urlArray = new JSONArray(sharedPreferences.getString(home_fragment.LAST_EXOURL_STATE,"[]"));
                    idArray = new JSONArray(sharedPreferences.getString(home_fragment.LAST_EXOID_STATE,"[]"));
                    MediaItemPosition = sharedPreferences.getInt(home_fragment.LAST_EXOPOSITEM_STATE,0);
                    int length = Math.min(titleArray.length(),Math.min(urlArray.length(),idArray.length()));
                    YoutubeMusicElement element;
                    for(int i = 0; i< length; i++){
                        if(titleArray.getString(i).equals("") || urlArray.getString(i).equals("") || idArray.getString(i).equals("")) continue;
                        element = new YoutubeMusicElement(titleArray.getString(i),idArray.getString(i),urlArray.getString(i));
                        MediaItem mediaItem = new MediaItem.Builder()
                                .setUri(urlArray.getString(i)).setTag(element)
                                .build();
                        singletonExoPlayer.getExoPlayer().addMediaItem(mediaItem);
                        singletonExoPlayer.getExoPlayer().prepare();
                        youtubeMusicModel.AddMusicElement(element);
                        if(i == MediaItemPosition){
                            Log.d("QuocBug", "onViewCreated: in loop: "+ String.valueOf(MediaItemPosition));
                            singletonExoPlayer.getExoPlayer().seekTo(MediaItemPosition,0);
                        }
                        else if(i == MediaItemPosition + 1)
                            singletonExoPlayer.getExoPlayer().play();
                    }
                    singletonExoPlayer.getExoPlayer().play();
                    youtubeMusicModel.setLastUpdateIndex(youtubeMusicModel.getLastUpdateIndex() + titleArray.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment!
        binding = MusicLayoutFragmentBinding.inflate(inflater,container,false);
        if(savedInstanceState != null)
        {
            page = savedInstanceState.getString(PAGE_MESSAGE);
            ImgQuery = savedInstanceState.getString(IMAGE_QUERY_MESSAGE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unsplashImgModel = ViewModelProviders.of(this).get(UnsplashImgModel.class);
        youtubeExecutor = new YoutubeExecutor(Objects.requireNonNull(getActivity()).getApplication());
        binding.ProgressBarID.setIndeterminateDrawable(new FoldingCube());
        UnsplashImgAdapter unsplashImgAdapter = new UnsplashImgAdapter(binding.getRoot().getContext(),binding.ProgressBarID);
        binding.RecyclerImgViewID.setAdapter(unsplashImgAdapter);
        binding.RecyclerImgViewID.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        CurrentSongNameTextView = binding.PlayerControlViewID.findViewById(R.id.musiclayouttitletrackID);
        NextSongNameTextView = binding.PlayerControlViewID.findViewById(R.id.NextSongTitletrackID);
        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(requireActivity().getApplication());
        binding.PlayerControlViewID.setPlayer(singletonExoPlayer.getExoPlayer());
        Log.d("QuocBug", "onViewCreated: in the oncreateview");
        // update data on UI
        if(singletonExoPlayer.getExoPlayer().getMediaItemCount() != 0){
            Log.d("QuocBug", "onViewCreated: Updating the UI");
            ExoPlayer exoPlayer = singletonExoPlayer.getExoPlayer();
            YoutubeMusicElement element = (YoutubeMusicElement) exoPlayer.getCurrentMediaItem().localConfiguration.tag;
            CurrentSongNameTextView.setText(element.getTitle());
            if(exoPlayer.getCurrentMediaItemIndex() + 1 < exoPlayer.getMediaItemCount()){
                element = (YoutubeMusicElement) Objects.requireNonNull(exoPlayer.getMediaItemAt(exoPlayer.getCurrentMediaItemIndex() + 1).localConfiguration).tag;
                NextSongNameTextView.setText(element.getTitle());
                youtubeMusicModel.setSuccesfulUpdateUI(true);
            }
            else{
                youtubeMusicModel.setSuccesfulUpdateUI(false);
                youtubeExecutor.MusicRecommendingExecutor(element.getMusicID(),new WeakReference<>(youtubeMusicModel),new WeakReference<>(NextSongNameTextView));
            }
        }

        // process the music:
        if(youtubeMusicModel.getLengthYoutubeList()==0 && !youtubeExecutor.isExecuting()){
            Log.d("QuocSoundcloud", "onViewCreated: ignite the Executor");
            singletonExoPlayer.EndMusic();
            singletonExoPlayer.setType(nameOfCategory);
            youtubeExecutor.MusicAsyncExecutor(MusicQuery,new WeakReference<>(youtubeMusicModel),new WeakReference<>(NextSongNameTextView));
        }
        // This is to add the listener to the singletonExoPlayer to update the UI depending on the MediaItem
        // the reason why when you backstack and come to the the same layout , getting the title with no changes is that
        // the current exoplayer does not clear the mediaList and the listener bellow isn't invoked
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
                        youtubeMusicModel.ResetModel();
                        youtubeExecutor.MusicAsyncExecutor(MusicQuery,new WeakReference<>(youtubeMusicModel),new WeakReference<>(NextSongNameTextView));
                    }
                }
            }
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Log.d("QuocBug", "onMediaItemTransition: In the Listener");
                if(mediaItem != null && mediaItem.localConfiguration != null){
                    int curIndex = singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex();
                    YoutubeMusicElement element = (YoutubeMusicElement) mediaItem.localConfiguration.tag;
                    if(curIndex == youtubeMusicModel.getLengthYoutubeList())
                    {
                        youtubeMusicModel.AddMusicElement(element);
                        // The youtubeExecutor should have been designed as a singleton. I'm too lazy to repair it now because it affects the whole project =((((
                        // the bellow is the slight hack to fix the bug =(( But it's not an optimized approach to fix it.
                        // TODO: Change the YoutubeExecutor into the singleton if you can in the future.
                        if(curIndex +1 < singletonExoPlayer.getExoPlayer().getMediaItemCount())
                            youtubeMusicModel.AddMusicElement((YoutubeMusicElement) singletonExoPlayer.getExoPlayer().getMediaItemAt(curIndex+1).localConfiguration.tag);
                    }
                    CurrentSongNameTextView.setText(element.getTitle());
                    Log.d("QuocBug", "onMediaItemTransition: title: "+ element.getTitle());
                    if(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() < singletonExoPlayer.getExoPlayer().getMediaItemCount() -1){
                        // this is NOT the last song in the list scope
                        youtubeMusicModel.setSuccesfulUpdateUI(true);
                        NextSongNameTextView.setText(youtubeMusicModel.getMusicElement(curIndex+1).getTitle());
                    }
                    else
                    {
                        // this is the last song in the list scope
                        youtubeMusicModel.setSuccesfulUpdateUI(false);
                        NextSongNameTextView.setText(R.string.loading);
                        youtubeExecutor.MusicRecommendingExecutor(element.getMusicID(),new WeakReference<>(youtubeMusicModel),new WeakReference<>(NextSongNameTextView));

                    }
                }
            }
        };
        singletonExoPlayer.getExoPlayer().addListener(listener);
        // This is invoked when the exoplayer begin a new mediaitem.
        // process the image:
        //Todo: Do optimization and cache the url of image in here to avoid the waste in API calls
        // We can use SQlite or SharedReference to locally cache the Urls (cache the UnsplashModel)

        if(unsplashImgModel.getRemainedNumber() == 0){
            binding.ProgressBarID.setVisibility(View.VISIBLE);
            new UnsplashAsynctask(unsplashImgAdapter,unsplashImgModel,ImgQuery,page).execute();
        }
        else{
            unsplashImgAdapter.setElement(unsplashImgModel.getCurrentElement());
        }

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.RIGHT){
                    binding.ProgressBarID.setVisibility(View.VISIBLE);
                    if(unsplashImgModel.getCur() !=0){
                        unsplashImgModel.PreviousOne();
                    }
                    unsplashImgAdapter.setElement(unsplashImgModel.getCurrentElement());
                }
                else if(direction == ItemTouchHelper.LEFT){
                    unsplashImgModel.NextOne();
                    binding.ProgressBarID.setVisibility(View.VISIBLE);
                    if(unsplashImgModel.getRemainedNumber()==0){
                        unsplashImgModel.UpdatePage();
                        new UnsplashAsynctask(unsplashImgAdapter,unsplashImgModel,ImgQuery,String.valueOf(unsplashImgModel.getCurPage())).execute();
                    }
                    else{
                        unsplashImgAdapter.setElement(unsplashImgModel.getCurrentElement());
                    }
                }
            }
        });
        helper.attachToRecyclerView(binding.RecyclerImgViewID);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PAGE_MESSAGE,page);
        outState.putString(IMAGE_QUERY_MESSAGE,ImgQuery);
    }

    @Override
    public void onPause() {
        super.onPause();
        SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication()).getExoPlayer().removeListener(listener);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> titleSet = new LinkedHashSet<>();
        Set<String> urlSet = new LinkedHashSet<>();
        Set<String> idSet = new LinkedHashSet<>();
        ExoPlayer exoPlayer = SingletonExoPlayer.getInstance(getActivity().getApplication()).getExoPlayer();
        for(int i=0; i< exoPlayer.getMediaItemCount(); i++){
            Log.d("QuocBug", "onPause: ");
            YoutubeMusicElement element = (YoutubeMusicElement) Objects.requireNonNull(exoPlayer.getMediaItemAt(i).localConfiguration).tag;
            titleSet.add(element.getTitle());
            urlSet.add(element.getDownloadedMusicUrl());
            idSet.add(element.getMusicID());
        }
        MediaItemPosition =exoPlayer.getCurrentMediaItemIndex();
        editor.putInt(home_fragment.LAST_EXOPOSITEM_STATE,MediaItemPosition);
        editor.putString(home_fragment.LAST_EXOTITLE_STATE,new JSONArray(titleSet).toString());
        editor.putString(home_fragment.LAST_EXOURL_STATE,new JSONArray(urlSet).toString());
        editor.putString(home_fragment.LAST_EXOID_STATE, new JSONArray(idSet).toString());
        editor.apply();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(isHappenBefore){
            Log.d("QuocBug", "onResume: The current length of the YoutubeModel: " + String.valueOf(youtubeMusicModel.getLengthYoutubeList()));
            SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(requireActivity().getApplication());
            MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
            if(item != null && item.localConfiguration != null)
            {
                YoutubeMusicElement currentElement = (YoutubeMusicElement) item.localConfiguration.tag;
                CurrentSongNameTextView.setText(currentElement.getTitle());
            }
            binding.PlayerControlViewID.setPlayer(singletonExoPlayer.getExoPlayer());
            singletonExoPlayer.getExoPlayer().addListener(listener);
        }
        isHappenBefore = true;
    }
}
