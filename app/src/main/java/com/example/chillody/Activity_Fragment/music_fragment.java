package com.example.chillody.Activity_Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chillody.Adapter.UnsplashImgAdapter;
import com.example.chillody.Model.FavoriteUnsplashImgViewModel;
import com.example.chillody.Model.FavoriteYoutubeElement;
import com.example.chillody.Model.FavoriteYoutubeViewModel;
import com.example.chillody.Model.GeneralYoutubeViewModel;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.UnsplashImgElement;
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
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.material.button.MaterialButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class music_fragment extends Fragment {
    private MusicLayoutFragmentBinding binding;
    private UnsplashImgModel unsplashImgModel;
    private YoutubeMusicModel youtubeMusicModel;
    private YoutubeExecutor youtubeExecutor;
    private FavoriteYoutubeViewModel favoriteYoutubeViewModel;
    private FavoriteUnsplashImgViewModel favoriteUnsplashImgViewModel;
    private String page;
    private String ImgQuery;
    private String MusicQuery;
    private String nameOfCategory;
    private int currentImagePage;
    private int currentImagePosition;
    private TextView CurrentSongNameTextView, NextSongNameTextView;
    private final static String PAGE_MESSAGE = "Here is the page message";
    private final static String IMAGE_QUERY_MESSAGE = "Here is the image query message";
    private Player.Listener listener;
    private SharedPreferences sharedPreferences;
    private int MediaItemPosition;
    private boolean isHappenBefore = false;
    private LiveData<List<YoutubeMusicElement>> youtubeSongLists;
    private final static String UNSPLASH_IMG_CURRENT_POS_FLAG ="NDQNTNUNSPLASHNDCIMG";
    private final static String UNSPLASH_IMG_CURRENT_CURRENT_PAGE_FLAG = "AhHyoSeopKimSeJongProposalBussiness";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        youtubeMusicModel = ViewModelProviders.of(this).get(YoutubeMusicModel.class);
        nameOfCategory = getArguments().getString("NameCategory");
        page = "1";
        switch (nameOfCategory){
            case "Chilling":
                ImgQuery = "sadness city";
                MusicQuery = "Ta da tung yeu nhau chua hao\n";
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
        MediaItemPosition = sharedPreferences.getInt(home_fragment.LAST_EXOPOSITEM_STATE,0);
        currentImagePage = sharedPreferences.getInt(UNSPLASH_IMG_CURRENT_CURRENT_PAGE_FLAG,1);
        currentImagePosition = sharedPreferences.getInt(UNSPLASH_IMG_CURRENT_POS_FLAG,0);
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
        favoriteUnsplashImgViewModel = ViewModelProviders.of(this).get(FavoriteUnsplashImgViewModel.class);
        favoriteYoutubeViewModel = ViewModelProviders.of(this).get(FavoriteYoutubeViewModel.class);
        unsplashImgModel = ViewModelProviders.of(this).get(UnsplashImgModel.class);
        GeneralYoutubeViewModel generalYoutubeViewModel = ViewModelProviders.of(this).get(GeneralYoutubeViewModel.class);
        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(requireActivity().getApplication());
        youtubeExecutor = new YoutubeExecutor(Objects.requireNonNull(getActivity()).getApplication());
        binding.ProgressBarID.setIndeterminateDrawable(new FoldingCube());
        UnsplashImgAdapter unsplashImgAdapter = new UnsplashImgAdapter(binding.getRoot().getContext(),binding.ProgressBarID,favoriteUnsplashImgViewModel);
        binding.RecyclerImgViewID.setAdapter(unsplashImgAdapter);
        binding.RecyclerImgViewID.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        CurrentSongNameTextView = binding.PlayerControlViewID.findViewById(R.id.musiclayouttitletrackID);
        NextSongNameTextView = binding.PlayerControlViewID.findViewById(R.id.NextSongTitletrackID);
        binding.PlayerControlViewID.setPlayer(singletonExoPlayer.getExoPlayer());

        if(!singletonExoPlayer.getType().equals(nameOfCategory))
        {
            singletonExoPlayer.EndMusic();
            singletonExoPlayer.setType(nameOfCategory);
                switch (nameOfCategory) {
                case "Chilling":
                    youtubeSongLists = generalYoutubeViewModel.getChillingSongs();
                    break;
                case "Cafe":
                    youtubeSongLists = generalYoutubeViewModel.getCafeSongs();
                    break;
                case "Ghibli":
                    youtubeSongLists = generalYoutubeViewModel.getGhibliSongs();
                    break;
                }
            youtubeSongLists.observe(getViewLifecycleOwner(), new Observer<List<YoutubeMusicElement>>() {
                @Override
                public void onChanged(List<YoutubeMusicElement> youtubeMusicElements) {
                    Log.d("PhuTest", "onChanged: observer change");
                    Log.d("PhuTest", "onChanged: the current size of list: "+ String.valueOf(singletonExoPlayer.getExoPlayer().getMediaItemCount()));
                    singletonExoPlayer.setType(nameOfCategory);
                    if(youtubeMusicElements.size() == 0){
                        Log.d("QuocTest", "onChanged: Trigger the observer");
                        youtubeExecutor.MusicAsyncExecutor(nameOfCategory,MusicQuery,new WeakReference<>(NextSongNameTextView));
                    }
                    else if(singletonExoPlayer.getExoPlayer().getMediaItemCount() < youtubeMusicElements.size()){
                        // This just is called when the loading the data from the database
                        Log.d("PhuTest", "onChanged: The total num of songs loaded is: "+ String.valueOf(youtubeMusicElements.size()));
                        Log.d("PhuTest", "onChanged: The MediaPosition is: "+ String.valueOf(MediaItemPosition));
                        MediaItem item;
                        List<MediaItem> items = new ArrayList<>();
                        for(int i=0 ;i< youtubeMusicElements.size(); i++){
                            item = new MediaItem.Builder().setUri(youtubeMusicElements.get(i).getDownloadedMusicUrl())
                                    .setTag(youtubeMusicElements.get(i)).build();
                            items.add(item);
                        }
                        Log.d("PhuTest", "onChanged: The size of the recent list of songs: "+ String.valueOf(singletonExoPlayer.getExoPlayer().getMediaItemCount()));
                        singletonExoPlayer.getExoPlayer().addMediaItems(items);
                        singletonExoPlayer.getExoPlayer().seekTo(MediaItemPosition,0);
                        singletonExoPlayer.getExoPlayer().prepare();
                        singletonExoPlayer.getExoPlayer().play();
                    }
                }
            });
        }
        // To update the UI if the type of singleton is corresponding to this category
        // why in the homefragment this block is ommited in the onviewcreated because there are no fragments before homefragment that can change the data in the singleton and need it to sync its UI
        // but the music fragment depends on the data of the home_fragment when it has the same type with the singleton.
        // Imagine that there is a case you are listening to music in homefragment and it moves to another song and suddenly you navigate to the music_fragment
        // of the category with the same type to the singleton recently and it need to update the UI. (this just like the explanation in the homefragment in the onresume)
        else{
            if(singletonExoPlayer.getExoPlayer().getMediaItemCount() != 0){
            Log.d("QuocBug", "onViewCreated: Updating the UI");
            ExoPlayer exoPlayer = singletonExoPlayer.getExoPlayer();
            YoutubeMusicElement element = (YoutubeMusicElement) Objects.requireNonNull(Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).localConfiguration).tag;
            CurrentSongNameTextView.setText(element.getTitle());
            if(element.isFavorite())
            {
                binding.LovingButton.setVisibility(View.GONE);
                binding.UnLovingButton.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.LovingButton.setVisibility(View.VISIBLE);
                binding.UnLovingButton.setVisibility(View.GONE);
            }
            if(exoPlayer.getCurrentMediaItemIndex() + 1 < exoPlayer.getMediaItemCount()){
                element = (YoutubeMusicElement) Objects.requireNonNull(exoPlayer.getMediaItemAt(exoPlayer.getCurrentMediaItemIndex() + 1).localConfiguration).tag;
                NextSongNameTextView.setText(element.getTitle());
                singletonExoPlayer.setUIUpdatingFlag(-1);
            }
            else{
                Log.d("QuocBug", "onViewCreated: Updating the UI: "+ String.valueOf(singletonExoPlayer.getUIUpdatingFlag()));
                singletonExoPlayer.setUIUpdatingFlag(singletonExoPlayer.getExoPlayer().getMediaItemCount());
                if(!singletonExoPlayer.isThreadProcessing())
                    youtubeExecutor.MusicRecommendingExecutor(nameOfCategory,element.getMusicID(),new WeakReference<>(NextSongNameTextView));
            }
        }
        }
        // This is to add the listener to the singletonExoPlayer to update the UI depending on the MediaItem
        // the reason why when you backstack and come to the the same layout , getting the title with no changes is that
        // the current exoplayer does not clear the mediaList and the listener bellow isn't invoked
        listener = new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
             // error occurs
                Log.d("QuocBug", "onPlayerError: PLAYERERROR IN MUSIC_FRAGMENT");
                Log.d("QuocBug", "onPlayerError: error name"+ error.getErrorCodeName());
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                CurrentSongNameTextView.setText("Loading...");
                if(item != null && item.localConfiguration != null){
                    YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                    Throwable cause = error.getCause();
                    if (cause instanceof HttpDataSource.HttpDataSourceException) {
                        Log.d("QuocBug", "onPlayerError: In the outer condition of the playerError");
                        HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                        if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                            Toast.makeText(binding.getRoot().getContext(), "Please wait a minute", Toast.LENGTH_LONG).show();
                            Log.d(" QuocBug", "onPlayerError: the link is error");
                            youtubeExecutor.failHandlingSong(element.getMusicID(),singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex());
                        }
                    }
                    // this just for the education purpose, the more efficient approach to this problem is to
                    // catch all errors regardless of its errorCode ... to load again and again
                    else if(error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND){
                        Log.d("QuocBug", "onPlayerError: Inside the Playback exception: ERROR_CODE_IO_FILE_NOT_FOUND");
                        Toast.makeText(binding.getRoot().getContext(), "There's a error! Please wait a minute", Toast.LENGTH_SHORT).show();
                        youtubeExecutor.failHandlingSong(element.getMusicID(),singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex());
                    }
                }
                // this when there is no item in the current position.
                // because after the mediaItem is in the ready state and play state, this method is invoked
                // therefore, theoretically this else is not gonna happen.
                else{
                    Log.d("QuocBug", "onPlayerError: no item in the current position");
                }
            }
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Log.d("QuocBug", "onMediaItemTransition: In the Listener");
                if(mediaItem != null && mediaItem.localConfiguration != null){
                    int curIndex = singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex();
                    YoutubeMusicElement element = (YoutubeMusicElement) mediaItem.localConfiguration.tag;
                    CurrentSongNameTextView.setText(element.getTitle());
                    if(element.isFavorite())
                    {
                        binding.LovingButton.setVisibility(View.GONE);
                        binding.UnLovingButton.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        binding.LovingButton.setVisibility(View.VISIBLE);
                        binding.UnLovingButton.setVisibility(View.GONE);
                    }
                    Log.d("QuocBug", "onMediaItemTransition: title: "+ element.getTitle());
                    if(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() < singletonExoPlayer.getExoPlayer().getMediaItemCount() -1){
                        // this is NOT the last song in the list scope

                        singletonExoPlayer.setUIUpdatingFlag(-1);
                        MediaItem item = singletonExoPlayer.getExoPlayer().getMediaItemAt(curIndex+1);
                        if(item.localConfiguration != null)
                        element = (YoutubeMusicElement) item.localConfiguration.tag;
                        NextSongNameTextView.setText(element.getTitle());
                    }
                    else
                    {
                        // this is the last song in the list scope
                        Log.d("QuocPhu", "onMediaItemTransition: not ready for changing+ "+ element.getTitle());
                        Log.d("QuocPhu", "onMediaItemTransition: The current position of the song is: "+ String.valueOf(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex()));
                        Log.d("QuocPhu", "onMediaItemTransition: The total number of songs in the list recently is: "+ String.valueOf(singletonExoPlayer.getExoPlayer().getMediaItemCount()));
                        singletonExoPlayer.setUIUpdatingFlag(singletonExoPlayer.getExoPlayer().getMediaItemCount());
                        NextSongNameTextView.setText(R.string.loading);
                        if(!singletonExoPlayer.isThreadProcessing())
                        youtubeExecutor.MusicRecommendingExecutor(nameOfCategory,element.getMusicID(),new WeakReference<>(NextSongNameTextView));
                    }
                }
                // 2022-03-08 15:16:36.438
            }
        };
        singletonExoPlayer.getExoPlayer().addListener(listener);
        // This is invoked when the exoplayer begin a new mediaitem.

        // process of adding song into the favorite list:
        binding.LovingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                // we need a condition here because, without the condition in case that when you are a new user clicking the category,
                //  it takes time to load more song into the list right... during that short time, if you click the button it will cause the bug
               // therefore, it need a condition here to prevent user to do that edge case.
                if(item != null && item.localConfiguration != null)
                {
                    binding.LovingButton.setVisibility(View.GONE);
                    binding.UnLovingButton.setVisibility(View.VISIBLE);
                    YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                    element.setFavorite(true);
                    favoriteYoutubeViewModel.InsertFavoriteSongs(new FavoriteYoutubeElement(element.getMusicID(), element.getDownloadedMusicUrl(), element.getTitle(), (!singletonExoPlayer.getType().contains("Love"))? singletonExoPlayer.getType()+"Love":singletonExoPlayer.getType()));
                    generalYoutubeViewModel.updateLikeSong(element);
                }
            }
        });
        binding.UnLovingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                // Contrary to the white button, in this case, the song is already loaded into the list (because the UnlovingButton is visible when the system detect the song is favorite :3 )
                // therefore, we do not need an if condition here ( because it is sure that the item is available and item.localconfiguration is not null :3 )
                YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                binding.UnLovingButton.setVisibility(View.GONE);
                binding.LovingButton.setVisibility(View.VISIBLE);
                element.setFavorite(false);
                favoriteYoutubeViewModel.DeleteSongElements(new FavoriteYoutubeElement(element.getMusicID(), element.getDownloadedMusicUrl(), element.getTitle(), (!singletonExoPlayer.getType().contains("Love"))? singletonExoPlayer.getType()+"Love":singletonExoPlayer.getType()));
                generalYoutubeViewModel.updateDislikeMusicElement(element);
            }
        });
        // This is for the case of orientation change... Because in the observer block, the if condition is set for the case in which we first navigate to the category
        if(unsplashImgModel.isContainSomething()){
            binding.ProgressBarID.setVisibility(View.VISIBLE);
            unsplashImgAdapter.setElement(unsplashImgModel.getCurrentElement());
        }
        // process the image:
        //Todo: Do optimization and cache the url of image in here to avoid the waste in API calls
        // We can use SQlite or SharedReference to locally cache the Urls (cache the UnsplashModel)
        // observe the data
        unsplashImgModel.getObservableUnsplashImgList(nameOfCategory).observe(getViewLifecycleOwner(), new Observer<List<UnsplashImgElement>>() {
            @Override
            public void onChanged(List<UnsplashImgElement> unsplashImgElements) {
                // This is for the first navigation to the category, when the system loads data from the database
                if(unsplashImgElements.size() == 0){
                    binding.ProgressBarID.setVisibility(View.VISIBLE);
                    Log.d("Ah", "onChanged: in the first if");
                    new UnsplashAsynctask(unsplashImgAdapter,unsplashImgModel,ImgQuery,page,nameOfCategory).execute();
                }
                else if(unsplashImgModel.getRemainedNumber() == 0){
                    Log.d("Ah", "onChanged: in the second condition");
                    binding.ProgressBarID.setVisibility(View.VISIBLE);
                    unsplashImgModel.setCurPage(currentImagePage);
                    unsplashImgModel.setCurPosition(currentImagePosition);
                    Log.d("Ah", "onChanged: the current position: "+ String.valueOf(unsplashImgModel.getCur()));
                    Handler handler = new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            unsplashImgAdapter.setElement((UnsplashImgElement) msg.obj);
                            Log.d("Ah", "handleMessage: the artist: "+ ((UnsplashImgElement) msg.obj).getArtistName());
                        }
                    };
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    service.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Ah", "run: The size of the unsplash list: "+ String.valueOf(unsplashImgElements.size()));
                            for(int i=0;i< unsplashImgElements.size();i++)
                            {
                                unsplashImgModel.addElement(unsplashImgElements.get(i));
                                if(i == unsplashImgModel.getCur()){
                                    Message message = new Message();
                                    message.obj = unsplashImgElements.get(i);
                                    handler.sendMessage(message);
                                }
                            }
                        }
                    });
                    service.shutdown();
                }
            }
        });

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
                        new UnsplashAsynctask(unsplashImgAdapter,unsplashImgModel,ImgQuery,String.valueOf(unsplashImgModel.getCurPage()),nameOfCategory).execute();
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
        ExoPlayer exoPlayer = SingletonExoPlayer.getInstance(getActivity().getApplication()).getExoPlayer();
        MediaItemPosition =exoPlayer.getCurrentMediaItemIndex();
        editor.putInt(home_fragment.LAST_EXOPOSITEM_STATE,MediaItemPosition);
        editor.putInt(UNSPLASH_IMG_CURRENT_CURRENT_PAGE_FLAG,unsplashImgModel.getCurPage());
        editor.putInt(UNSPLASH_IMG_CURRENT_POS_FLAG,unsplashImgModel.getCur());
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
                if(currentElement.isFavorite())
                {
                    binding.LovingButton.setVisibility(View.GONE);
                    binding.UnLovingButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    binding.LovingButton.setVisibility(View.VISIBLE);
                    binding.UnLovingButton.setVisibility(View.GONE);
                }
            }
            binding.PlayerControlViewID.setPlayer(singletonExoPlayer.getExoPlayer());
            singletonExoPlayer.getExoPlayer().addListener(listener);
        }
        isHappenBefore = true;
    }

}
