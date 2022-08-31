package com.example.chillody.Activity_Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chillody.Adapter.FavSongAdapter;
import com.example.chillody.Model.FavoriteRecyclerViewManager;
import com.example.chillody.Model.FavoriteYoutubeElement;
import com.example.chillody.Model.FavoriteYoutubeViewModel;
import com.example.chillody.Model.GeneralYoutubeViewModel;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.example.chillody.R;
import com.example.chillody.databinding.FavMusicLayoutBinding;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO: The song in the the list outside of loving list does not sync with that song in the loving list. For example, from the Chilling category, if you are listening to
//  "See tinh - Hoang Thuy Linh", if you love it and navigate the loving fragment, the play/pause toggle will not be synced. (You can try your self)
//  this because the loving songs is maintained in another database other than the database currently storing that chilling song "see tinh - hoang thuy linh"
//  In the future you can merge them together and implement the mechanism for syncing them.

public class Fav_Music_fragment extends Fragment {
    private FavMusicLayoutBinding binding;
    private FavSongAdapter ChillingAdapter,CafeAdapter,GhibliAdapter;
    private final TextView NameSong;
    private final ImageView redBtn,whiteBtn;
    private boolean ChillingIsWhiteClick = false, CafeIsWhiteClick = false, GhibliIsWhiteClick = false;
    private FavoriteRecyclerViewManager favoriteRecyclerViewManager;
    private SingletonExoPlayer singletonExoPlayer;
    private boolean isHappenBefore = false;
    private Player.Listener listener;
    private boolean isUIStopUpdating = false;
    public Fav_Music_fragment(TextView nameSong,ImageView whiteBtn,ImageView redBtn){
        this.NameSong = nameSong;
        this.redBtn = redBtn;
        this.whiteBtn = whiteBtn;
    }
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
        singletonExoPlayer = SingletonExoPlayer.getInstance(requireActivity().getApplication());
        FavoriteYoutubeViewModel favoriteYoutubeViewModel = ViewModelProviders.of(this).get(FavoriteYoutubeViewModel.class);
        GeneralYoutubeViewModel generalYoutubeViewModel = ViewModelProviders.of(this).get(GeneralYoutubeViewModel.class);
        Log.d("QuocMusicFragment", "onViewCreated: Creating");
        favoriteRecyclerViewManager = new FavoriteRecyclerViewManager(binding.ChillingfavmusiclistID, binding.CafefavmusiclistID, binding.GhiblifavmusiclistID);

        ChillingAdapter = new FavSongAdapter(binding.getRoot().getContext(), requireActivity().getApplication(), favoriteYoutubeViewModel, generalYoutubeViewModel,NameSong,whiteBtn,redBtn,favoriteRecyclerViewManager);
        binding.ChillingfavmusiclistID.setAdapter(ChillingAdapter);
        binding.ChillingfavmusiclistID.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        CafeAdapter = new FavSongAdapter(binding.getRoot().getContext(),getActivity().getApplication(), favoriteYoutubeViewModel, generalYoutubeViewModel,NameSong,whiteBtn,redBtn,favoriteRecyclerViewManager);
        binding.CafefavmusiclistID.setAdapter(CafeAdapter);
        binding.CafefavmusiclistID.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        GhibliAdapter = new FavSongAdapter(binding.getRoot().getContext(),getActivity().getApplication(), favoriteYoutubeViewModel, generalYoutubeViewModel,NameSong,whiteBtn,redBtn,favoriteRecyclerViewManager);
        binding.GhiblifavmusiclistID.setAdapter(GhibliAdapter);
        binding.GhiblifavmusiclistID.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        favoriteYoutubeViewModel.getFavChillingSongs().observe(getViewLifecycleOwner(), new Observer<List<FavoriteYoutubeElement>>() {
            @Override
            public void onChanged(List<FavoriteYoutubeElement> favoriteYoutubeElements) {
                if(favoriteYoutubeElements.size()==0) binding.ChillingmusictextviewID.setVisibility(View.GONE);
                else binding.ChillingmusictextviewID.setVisibility(View.VISIBLE);
                ChillingAdapter.setCurrentList(favoriteYoutubeElements);
                favoriteRecyclerViewManager.setChillingSize(favoriteYoutubeElements.size());
                Log.d("Asshit", "onChanged: The size of favelements: "+ String.valueOf(favoriteYoutubeElements.size()));
                if(ChillingIsWhiteClick){
                    ChillingIsWhiteClick = false;
                    favoriteRecyclerViewManager.setFlag(singletonExoPlayer.getType());
                    favoriteRecyclerViewManager.setPosition(ChillingAdapter.getItemCount()-1);
                    // This is bug because when we click the heart icon and the dataset in the adapter is changed. It needs a bit of time to prepare and load a view to the new data item in the adapter.
                    // But in the old way, after the data changes, we try to update the UI component of the new data item ( the pause and play icon); even though the number of item of the viewholder or adapter is updated but we are not sure the view is loaded.
                    // Therefore, the solution to this problem is that, we procrastinate the process of updating the UI component by put it to another thread and sleep that thread for a bit of time.
                    // But the problem is that, our needed execution is related to the view of UI thread.... We can't put it in the worker thread (it violates the rule not accessing the UI component on another thread).
                    // Therefore, we can solve this via a Handler object.
                    // This handler object is an immediate element that helps one thread to communicate with another thread
                    // We can solve the problem via the Runnable object and the method handler.post(runnable,time) ( Note that: This post method will run on the thread that gives birth to the Handler object.
                    // Normally, in the handler, we do need a looper (more detailed in the notion file), but in the UI thread we do have an available looper so we do need to define anything (you can write it explicitly the getUIlooper or write nothing ... it's okay)
                    // The package that can be moved to the Handler to be run on the thread that gives birth to that handler, is the message object or runnable object
                    // with the message object, the execution is defined in the handler... but with the runnable object you have to define it every time you want to use it.
                    // In the summary, he key solution to this problem is that, you can procrastinate process of updating UI of the adpater by
                    // define another thread,... sleep that thread for a bit of time (you can do this via ExecutorService object or Thread object) and move the executing to the UI thread (because we are processing the UI components)
                     // packaged in the message or runnable object via the handler.
                    // or we can define the executing thing in the runnable object and set time to implement it in the UI thread via the method Handler.pos(runnable,time)
                    // if you do this way... it will block the UI thread.
//                    Handler handler = new Handler();
//                    Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(1000);
//                                if(favoriteRecyclerViewManager.getCurrentChildView() != null)
//                                {
//                                    favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.VISIBLE);
//                                    favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.GONE);
//                                }
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    };
//                    handler.post(runnable);
                    // it will not block the UI thread... this will act as the alarmer ... to set the time for the execution in the UI thread.
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                                if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                                {
                                    favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.VISIBLE);
                                    favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.GONE);
                                }
                        }
                    };
                    handler.postDelayed(runnable,1000);

                    // this is another solution.
//                    Handler handler = new Handler(Looper.getMainLooper()){
//                        @Override
//                        public void handleMessage(@NonNull Message msg) {
//                            super.handleMessage(msg);
//                                if(favoriteRecyclerViewManager.getCurrentChildView() != null)
//                                {
//                                    favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.VISIBLE);
//                                    favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.GONE);
//                                }
//                        }
//                    };
//                    ExecutorService service = Executors.newFixedThreadPool(1);
//                    service.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(1000);
//                                Message message = new Message();
//                                handler.sendMessage(message);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    service.shutdown();
                }
                Log.d("QuocLiveChange", "onChanged: chillingnumber: "+String.valueOf(favoriteYoutubeElements.size()));
            }
        });
        favoriteYoutubeViewModel.getFavCafeSongs().observe(getViewLifecycleOwner(), new Observer<List<FavoriteYoutubeElement>>() {
            @Override
            public void onChanged(List<FavoriteYoutubeElement> favoriteYoutubeElements) {
                if(favoriteYoutubeElements.size() ==0) binding.CafemusictextviewID.setVisibility(View.GONE);
                else binding.CafemusictextviewID.setVisibility(View.VISIBLE);
                CafeAdapter.setCurrentList(favoriteYoutubeElements);
                favoriteRecyclerViewManager.setCafeSize(favoriteYoutubeElements.size());
                if(CafeIsWhiteClick){
                    CafeIsWhiteClick = false;
                    favoriteRecyclerViewManager.setFlag(singletonExoPlayer.getType());
                    favoriteRecyclerViewManager.setPosition(CafeAdapter.getItemCount()-1);
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                            {
                                favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.VISIBLE);
                                favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.GONE);
                            }
                        }
                    };
                    handler.postDelayed(runnable,1000);
                }
                Log.d("QuocLiveChange", "onChanged: cafenumber: "+String.valueOf(favoriteYoutubeElements.size()));
            }
        });
        favoriteYoutubeViewModel.getFavGhibliSongs().observe(getViewLifecycleOwner(), new Observer<List<FavoriteYoutubeElement>>() {
            @Override
            public void onChanged(List<FavoriteYoutubeElement> favoriteYoutubeElements) {
                if(favoriteYoutubeElements.size() == 0) binding.GhiblimusictextviewID.setVisibility(View.GONE);
                else binding.GhiblimusictextviewID.setVisibility(View.VISIBLE);
                GhibliAdapter.setCurrentList(favoriteYoutubeElements);

                favoriteRecyclerViewManager.setGhibliSize(favoriteYoutubeElements.size());
                if(GhibliIsWhiteClick){
                    GhibliIsWhiteClick = false;
                    favoriteRecyclerViewManager.setFlag(singletonExoPlayer.getType());
                    favoriteRecyclerViewManager.setPosition(GhibliAdapter.getItemCount()-1);
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                            {
                                favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.VISIBLE);
                                favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.GONE);
                            }
                        }
                    };
                    handler.postDelayed(runnable,1000);
                }
                Log.d("QuocLiveChange", "onChanged: ghiblinumber: "+String.valueOf(favoriteYoutubeElements.size()));
            }
        });

        whiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                if(item != null && item.localConfiguration != null){
                    YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                    whiteBtn.setVisibility(View.GONE);
                    redBtn.setVisibility(View.VISIBLE);
                    // I don't know why the YoutubeMusicElement in the tag is really the real instance ...
                    // this is so magic.. I though that it's just a hashed object.
                    element.setFavorite(true);
                    if(singletonExoPlayer.getType().contains("Love")){
                        switch (singletonExoPlayer.getType()){
                            case "ChillingLove":
                                ChillingIsWhiteClick = true;
                                CafeIsWhiteClick = false;
                                GhibliIsWhiteClick = false;
                                break;
                            case "CafeLove":
                                ChillingIsWhiteClick = false;
                                CafeIsWhiteClick = true;
                                GhibliIsWhiteClick = false;
                                break;
                            case "GhibliLove":
                                ChillingIsWhiteClick = false;
                                CafeIsWhiteClick = false;
                                GhibliIsWhiteClick = true;
                                break;
                        }
                        singletonExoPlayer.getExoPlayer().moveMediaItem(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex(),singletonExoPlayer.getExoPlayer().getMediaItemCount()-1);
                        if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                        {
                            favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.GONE);
                            favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.VISIBLE);
                        }
                        favoriteRecyclerViewManager.setFlag(singletonExoPlayer.getType());
                        favoriteRecyclerViewManager.setPosition(-1);
                    }
                    Log.d("cak", "onClick: WHITE");
                    favoriteYoutubeViewModel.InsertFavoriteSongs(new FavoriteYoutubeElement(element.getMusicID(), element.getDownloadedMusicUrl(), element.getTitle(), (!singletonExoPlayer.getType().contains("Love"))? singletonExoPlayer.getType()+"Love":singletonExoPlayer.getType()));
                    generalYoutubeViewModel.updateLikeSong(element);
                }
            }
        });
        redBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Luc", "onClick: In the love ");
                MediaItem item = singletonExoPlayer.getExoPlayer().getCurrentMediaItem();
                whiteBtn.setVisibility(View.VISIBLE);
                redBtn.setVisibility(View.GONE);
                if(singletonExoPlayer.getType().contains("Love")){
                    singletonExoPlayer.getExoPlayer().moveMediaItem(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex(),singletonExoPlayer.getExoPlayer().getMediaItemCount()-1);
                    if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                    {
                        favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.GONE);
                        favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.VISIBLE);
                    }
                    favoriteRecyclerViewManager.setFlag(singletonExoPlayer.getType());
                    favoriteRecyclerViewManager.setPosition(-1);
                }
                YoutubeMusicElement element = (YoutubeMusicElement) item.localConfiguration.tag;
                element.setFavorite(false);
                Log.d("cak", "onClick: RED");
                favoriteYoutubeViewModel.DeleteSongElements(new FavoriteYoutubeElement(element.getMusicID(), element.getDownloadedMusicUrl(), element.getTitle(), (!singletonExoPlayer.getType().contains("Love"))? singletonExoPlayer.getType()+"Love":singletonExoPlayer.getType()));
                generalYoutubeViewModel.updateDislikeMusicElement(element);
            }
        });
        if(singletonExoPlayer.getType().contains("Love")){
            Handler handler = new Handler(Looper.getMainLooper());

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    favoriteRecyclerViewManager.setFlag(singletonExoPlayer.getType());
                    favoriteRecyclerViewManager.setPosition(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex());
                    Log.d("mamamia", "onViewCreated: In loop");
                    if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                    {
                        if(singletonExoPlayer.getExoPlayer().isPlaying()) {
                            favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.VISIBLE);
                            favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.GONE);
                        }
                        else{
                            favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.GONE);
                            favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.VISIBLE);
                        }
                    }
                }
            };
            handler.postDelayed(runnable,1000);
        }
        Log.d("mamamia", "onViewCreated: type: "+String.valueOf(favoriteRecyclerViewManager.getType()));
        Log.d("mamamia", "onViewCreated: position: "+String.valueOf(favoriteRecyclerViewManager.getPosition()));

        listener = new Player.Listener() {
           @Override
           public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
               if(singletonExoPlayer.getType().contains("Love")){
                   if(singletonExoPlayer.getType().equals(favoriteRecyclerViewManager.getType()) && singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex() == favoriteRecyclerViewManager.getPosition()) {
                       return;
                   }
                       Log.d("stress", "onIsPlayingChanged: next song, the type: "+ String.valueOf(singletonExoPlayer.getType()) + " the index: "+ String.valueOf(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex()));
                   if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                   {
                       favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.GONE);
                       favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.VISIBLE);
                   }
                   favoriteRecyclerViewManager.setFlag(singletonExoPlayer.getType());
                   favoriteRecyclerViewManager.setPosition(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex());
                   // this is for the case when you add the new song into the the loving list and you move to the loving list and play that song
                   // Because in the processing in the FavSongAdapter .... the system has to take time to add all the songs into the exoplayer, simultaneously playing the exoplayer
                   // then it update the manager
                   // but when the exoplayer play, this is called immediately causing avoriteRecyclerViewManager.getCurrentChildView()  = null
                   if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                   {
                       favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.VISIBLE);
                       favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.GONE);
                   }
                   isUIStopUpdating = false;
               }
           }

           @Override
           public void onIsPlayingChanged(boolean isPlaying) {
              if(singletonExoPlayer.getType().contains("Love")){
                  Log.d("stress", "onIsPlayingChanged: listen for onplaying, the type: "+ String.valueOf(singletonExoPlayer.getType())+ " the index: "+ String.valueOf(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex()));
                  if(isPlaying){
                      Log.d("stress", "onIsPlayingChanged: isplaying, the type: "+ String.valueOf(singletonExoPlayer.getType())+ " the index: "+ String.valueOf(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex()));
                      if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                      {
                          favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.GONE);
                          favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.VISIBLE);
                      }
                      Log.d("stress", "onIsPlayingChanged: QUOCKHUM, the type: "+ String.valueOf(singletonExoPlayer.getType())+ " the index: "+ String.valueOf(favoriteRecyclerViewManager.getPosition()));
                      favoriteRecyclerViewManager.setFlag(singletonExoPlayer.getType());
                      favoriteRecyclerViewManager.setPosition(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex());
                      if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                      {
                          favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.VISIBLE);
                          favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.GONE);
                      }
                  }
                  else if(isUIStopUpdating){
                      Log.d("stress", "onIsPlayingChanged: STOP ");
                      if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                      {
                          favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.GONE);
                          favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.VISIBLE);
                      }
                  }
                  isUIStopUpdating = true;
              }
           }

           @Override
           public void onPlayerError(PlaybackException error) {
               if(singletonExoPlayer.getType().contains("Love")) {
                   Log.d("stress", "onPlayerError: Pause");
                   if(favoriteRecyclerViewManager.getCurrentChildView() != null)
                   {
                       favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PauseID).setVisibility(View.GONE);
                       favoriteRecyclerViewManager.getCurrentChildView().findViewById(R.id.PlayID).setVisibility(View.VISIBLE);
                   }
               }
           }
       };
        singletonExoPlayer.getExoPlayer().addListener(listener);
    }
    // why the onDestroy is not called here ... it because this in fact not a real fragment
    // this just a fragment living in the adapter ... therefore, when it's canceled out... it will not invoke
    // the onDestroy() ... Why it leads to this still remains mysterious.

    @Override
    public void onPause() {
        super.onPause();
        Log.d("QuocMusicFragment", "onPause: Pausing");
        singletonExoPlayer.getExoPlayer().removeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("QuocMusicFragment", "onResume: Resumming");
        if(isHappenBefore){
            if(singletonExoPlayer.getType().contains("Love")){

            }
            singletonExoPlayer.getExoPlayer().addListener(listener);
        }
        isHappenBefore = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("QuocMusicFragment", "onResume: stopping");
    }
}