package com.example.chillody.Activity_Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import com.example.chillody.Model.GeneralYoutubeViewModel;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.example.chillody.Model.categoryObj;
import com.example.chillody.Networking.YoutubeExecutor;
import com.example.chillody.R;
import com.example.chillody.databinding.HomeLayoutFragmentBinding;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// lifecycle of fragment :
/*
* when we move the whole app into the background task -> then it will onpause, and then if we start it again it will go for onresume
* note that the onresume will be invoked even when the fragment is started the first time.
* when we move to another fragment or activity, if we backstack it will go from the onviewcreate first, then onviewcreated and onresume ....
* */
public class home_fragment extends Fragment {
    public final static String sharedFile ="com.example.chillody.Activity_Fragment";
    public final static String LAST_EXOTYPE_STATE = "NDQEXOPLAYERTYPE";
    public final static String LAST_EXOPOSITEM_STATE = "NDQEXOPLAYERITEMPOSITIONSTATE";
    private SharedPreferences sharedPreferences;
    private GeneralYoutubeViewModel generalYoutubeViewModel;
    private final List<categoryObj> categoryObjList = new ArrayList<>();
    private HomeLayoutFragmentBinding binding;
    private TextView titleTrackTextview;
    private Player.Listener listener;
    private String exoPlayerType;
    private int MediaItemPosition;
    private boolean isHappenBefore = false;
    private ImageView RedHeartIcon,WhiteHeartIcon;
    private LiveData<List<YoutubeMusicElement>> listSongs;
    public static FavoriteYoutubeViewModel favoriteYoutubeViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("QuocLife", "HomeFragment: onCreate: ");
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(sharedFile,Context.MODE_PRIVATE);
        categoryObjList.add(new categoryObj("Chilling","https://c0.wallpaperflare.com/preview/259/493/306/person-car-cloud-sunset.jpg"));
        categoryObjList.add(new categoryObj("Cafe","https://c4.wallpaperflare.com/wallpaper/805/668/874/lofi-neon-coffee-house-shop-neon-glow-hd-wallpaper-preview.jpg"));
        categoryObjList.add(new categoryObj("Ghibli","https://studioghiblimovies.com/wp-content/uploads/2020/03/barcode-scanners-qr-code-2d-code-creative-barcode.jpg"));
        exoPlayerType = sharedPreferences.getString(LAST_EXOTYPE_STATE,"");
        MediaItemPosition = sharedPreferences.getInt(LAST_EXOPOSITEM_STATE,0);
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
        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication());
        binding.styledPlayerControlView.setPlayer(singletonExoPlayer.getExoPlayer());
        generalYoutubeViewModel = ViewModelProviders.of(this).get(GeneralYoutubeViewModel.class);
        titleTrackTextview = binding.styledPlayerControlView.findViewById(R.id.titletrackID);

        // !ishappen here is to restrict the condition such that the block code will be implemented only once at the intitial time
        // otherwise, when it move to another activity and backstack again, this callback (onviewcreated will be invoked again) ... thus
        // this method will be called again leading to it will register another observer to the data causing the malfunction of the app.
        if(!exoPlayerType.equals("") && !exoPlayerType.contains("Love") && !isHappenBefore){
            singletonExoPlayer.setType(exoPlayerType);
            Log.d("QuocKhung", "onViewCreated: ignite");
            switch(exoPlayerType){
                case "Chilling":
                    listSongs = generalYoutubeViewModel.getChillingSongs();
                    break;
                case "Cafe":
                    listSongs = generalYoutubeViewModel.getCafeSongs();
                    break;
                case "Ghibli":
                    listSongs = generalYoutubeViewModel.getGhibliSongs();
                    break;
            }
            // when in the database, we do not have any data stored in that
            // this observe method will not be invoked.
            listSongs.observe(getViewLifecycleOwner(), new Observer<List<YoutubeMusicElement>>() {
                @Override
                public void onChanged(List<YoutubeMusicElement> youtubeMusicElements) {
                    Log.d("PhuTest", "onChanged: dataset changes");
                    if(singletonExoPlayer.getExoPlayer().getMediaItemCount() != youtubeMusicElements.size())
                    {
                        MediaItem item;
                        // The reason why we need a list of items and address this problem this way is because of the magical behavior in which
                        // when singleton.addMediaItem()... it invokes the onMediaItemTransition(). Unfortunately, this accidentally invokes
                        // the method to load more song... ( please access ShindraTensei account and click the "muc da luu" category for more information)
                        List<MediaItem> items = new ArrayList<>();
                        for(int i=0; i< youtubeMusicElements.size(); i++){
                            item = new MediaItem.Builder().setUri(youtubeMusicElements.get(i).getDownloadedMusicUrl())
                                    .setTag(youtubeMusicElements.get(i)).build();
                            items.add(item);
                        }
                        singletonExoPlayer.getExoPlayer().addMediaItems(items);
                        singletonExoPlayer.getExoPlayer().prepare();
                        singletonExoPlayer.getExoPlayer().seekTo(MediaItemPosition,0);
                        singletonExoPlayer.getExoPlayer().play();
                    }
                    // this is when singleton sync with the youtubeMusicElement
                    // this will be called in the case when you are listening to music and suddenly the oncreate will be called again.
                    // this method is sure to happen when the data in the singleton syncs with those of database.
                    // but it will conflict with the feature which re-set the the error song in the current list.
                    // just in this fragment.... we process thing this way... because other fragments navigates to this fragment... in other words, this fragment plays a role as an intersection
                    // everytime, this fragment move to another fragment the
//                    else if(singletonExoPlayer.getExoPlayer().getMediaItemCount() !=0)
//                    {
//                        Log.d("Luc", "onChanged: in the observer else if--- So strange");
//                        if(!singletonExoPlayer.IsErrorProcessed())
//                        {
////                            MediaItem  item = singletonExoPlayer.getExoPlayer().getMediaItemAt(MediaItemPosition);
////                            YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
////                            titleTrackTextview.setText(element.getTitle());
////                            Log.d("PhuTest", "onChanged: The newly added song is: "+ element.getTitle());
//                        }
//                        else singletonExoPlayer.setErrorProcessedFlag(false);
//                    }
                    // The reason why I think we should delete the above code is that even when we go to the next fragment ....
                    // the home fragment will still remain the same... ( I mean it's life will be preserved in the system)
                    // I guess the ViewModel will not be deleted ... I mean even though we init it with the new ViewModel object with ViewModel.of...
                    // it will be the old instance ... Therefore, we don't need to do the above block... ( I guess in the past... you though that the generalYoutubeViewModel
                    // will be reset every navigation ( because when navigating to another fragment and back, the onViewCreated and onCreateView will be re-invoked
                    // therefore, the generalYoutubeViewModel will be reset, thus it will re-observe ... but at that time , the current data in the database has the same size with which in the singletonExoplayer)

                }
            });
        }
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
        RedHeartIcon = binding.styledPlayerControlView.findViewById(R.id.heartREDbtnID);
        WhiteHeartIcon = binding.styledPlayerControlView.findViewById(R.id.heartbtnID);

        favoriteYoutubeViewModel = ViewModelProviders.of(this).get(FavoriteYoutubeViewModel.class);
        Log.d("QuocBug", "onViewCreated: in the home_fragment of the onviewcreated");
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
                    favoriteYoutubeViewModel.InsertFavoriteSongs(new FavoriteYoutubeElement(element.getMusicID(), element.getDownloadedMusicUrl(), element.getTitle(), (!singletonExoPlayer.getType().contains("Love"))? singletonExoPlayer.getType()+"Love":singletonExoPlayer.getType()));
                    generalYoutubeViewModel.updateLikeSong(element);
                }
            }
        });
        RedHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("QuocClick", "onClick: HOME");
                WhiteHeartIcon.setVisibility(View.VISIBLE);
                RedHeartIcon.setVisibility(View.GONE);
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                // we dont need to check if the item here is null because if it's null then
                // the White Button can't be clicked. Hence, the red one can not appear
                // due to the default layout.
                YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                element.setFavorite(false);
                favoriteYoutubeViewModel.DeleteSongElements(new FavoriteYoutubeElement(element.getMusicID(), element.getDownloadedMusicUrl(), element.getTitle(), (!singletonExoPlayer.getType().contains("Love"))? singletonExoPlayer.getType()+"Love":singletonExoPlayer.getType()));
                generalYoutubeViewModel.updateDislikeMusicElement(element);
            }
        });
        listener = new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                // error occurs
                // if this happens ... it will reload the list regardless of what type of error it is
                //this maybe the very bad implementation but who knows :>>
                Log.d("QuocBug", "onPlayerError: error name"+ error.getErrorCodeName());
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                if(item!= null && item.localConfiguration != null){
                    YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                    Toast.makeText(binding.getRoot().getContext(), "Please wait a minute", Toast.LENGTH_LONG).show();
                    titleTrackTextview.setText("Loading...");
                    new YoutubeExecutor(Objects.requireNonNull(getActivity()).getApplication()).failHandlingSong(element.getMusicID(),singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex());
                }
            }
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                if(mediaItem != null && mediaItem.localConfiguration != null){
                    Log.d("PhuTest", "onMediaItemTransition: current "+ String.valueOf(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex()) +" \n the total sum is: "+ String.valueOf(singletonExoPlayer.getExoPlayer().getMediaItemCount()));
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
       ExoPlayer exoPlayer = SingletonExoPlayer.getInstance(getActivity().getApplication()).getExoPlayer();
       exoPlayerType = SingletonExoPlayer.getInstance(getActivity().getApplication()).getType();
       MediaItemPosition =exoPlayer.getCurrentMediaItemIndex();
       editor.putInt(LAST_EXOPOSITEM_STATE,MediaItemPosition);
        Log.d("QuocBug", "onPause: MediaPosition: "+String.valueOf(MediaItemPosition));
       editor.putString(LAST_EXOTYPE_STATE,exoPlayerType);
       editor.apply();
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("QuocLife", "HomeFragment: onResume: "+ String.valueOf(isHappenBefore));
        // isHappenBefore is to restrict this callback to not be called at the first time( because as mentioned above, when the system first create this fragment , the onresume() is still called)
        if(isHappenBefore){
            Log.d("QuocLife", "onResume: Inside resume");
              SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication());
              binding.styledPlayerControlView.setPlayer(singletonExoPlayer.getExoPlayer());
              MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
            // This is to update the UI, sync the data of ExoPlayer of category fragment into this fragment
            // this method is for the case in which we are listening to the music in the music-fragment until it move to the next song (we wait a couple seconds
            // before backstacking it) -> this case the onMediaitemtransition will not be invoked (the onresumed will be called again and ishappenhere is true therefore, it will implement inside the block so we put this block at here)-> cannot update the UI
            // This method is for that case -> to update UI and do some operation i.e it plays a role as the onMediatItemTransition() (because in this case this listener cannot be heard)
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
            if(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() == singletonExoPlayer.getExoPlayer().getMediaItemCount()-1 && !singletonExoPlayer.isThreadProcessing()){
                if(singletonExoPlayer.getType().contains("Love")) return;
                Log.d("QuocMusic", "onPlaybackStateChanged: Loading more song");
                YoutubeMusicElement LastElement = (YoutubeMusicElement) Objects.requireNonNull(singletonExoPlayer.getExoPlayer().getCurrentMediaItem().localConfiguration).tag;
                new YoutubeExecutor(getActivity().getApplication()).MusicRecommendingExecutor(singletonExoPlayer.getType(), LastElement.getMusicID(),null);
            }
            singletonExoPlayer.getExoPlayer().addListener(listener);
        }
        isHappenBefore = true;
    }

}