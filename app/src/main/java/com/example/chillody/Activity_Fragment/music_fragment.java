package com.example.chillody.Activity_Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import android.widget.ImageView;

import com.example.chillody.Adapter.UnsplashImgAdapter;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.SoundCloudMusicModel;
import com.example.chillody.Model.YoutubeMusicModel;
import com.example.chillody.Networking.SoundCloudExecutor;
import com.example.chillody.Networking.UnsplashAsynctask;
import com.example.chillody.Model.UnsplashImgModel;
import com.example.chillody.Networking.YoutubeExecutor;
import com.example.chillody.databinding.MusicLayoutFragmentBinding;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.exoplayer2.ExoPlayer;

import java.lang.ref.WeakReference;
import java.util.Objects;


public class music_fragment extends Fragment {
    private MusicLayoutFragmentBinding binding;
    private UnsplashImgModel unsplashImgModel;
   // private SoundCloudMusicModel soundCloudMusicModel;
   // private SoundCloudExecutor soundCloudExecutor;
    private YoutubeMusicModel youtubeMusicModel;
    private YoutubeExecutor youtubeExecutor;
    private String page;
    private String ImgQuery;
    private String MusicQuery;
    private ExoPlayer exoPlayer;
    private ImageView Play_Pause_Button;
    private final static String PAGE_MESSAGE = "Here is the page message";
    private final static String IMAGE_QUERY_MESSAGE = "Here is the image query message";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String nameOfCategory = getArguments().getString("NameCategory");
        page = "1";
        switch (nameOfCategory){
            case "Chilling":
                ImgQuery = "street night";
                MusicQuery = "Indie VietNam";
                break;
            case "Ghibli":
                ImgQuery = "kyoto";
                MusicQuery = "Ghibli Chill";
                break;
            case "Cafe":
                ImgQuery = "cozy cafe";
                MusicQuery = "An Coong - Piano Cover - Collection";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nameOfCategory);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment!
        binding = MusicLayoutFragmentBinding.inflate(inflater,container,false);
        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(Objects.requireNonNull(getActivity()).getApplication());
        exoPlayer = singletonExoPlayer.getExoPlayer();
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
       // soundCloudMusicModel = ViewModelProviders.of(this).get(SoundCloudMusicModel.class);
        // soundCloudExecutor = ViewModelProviders.of(this).get(SoundCloudExecutor.class);
        youtubeMusicModel = ViewModelProviders.of(this).get(YoutubeMusicModel.class);
        youtubeExecutor = ViewModelProviders.of(this).get(YoutubeExecutor.class);
        binding.ProgressBarID.setIndeterminateDrawable(new FoldingCube());
        UnsplashImgAdapter unsplashImgAdapter = new UnsplashImgAdapter(binding.getRoot().getContext(),binding.ProgressBarID);
        binding.RecyclerImgViewID.setAdapter(unsplashImgAdapter);
        binding.RecyclerImgViewID.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        // process the music:
        if(youtubeMusicModel.getLastUpdateIndex()==0 && !youtubeExecutor.isExecuting()){
            Log.d("QuocSoundcloud", "onViewCreated: ignite the Executor");
          //  soundCloudExecutor.MusicProcess(MusicQuery,new WeakReference<>(soundCloudMusicModel));
            youtubeExecutor.MusicAsyncExecutor(MusicQuery,new WeakReference<>(youtubeMusicModel),new WeakReference<>(binding.PlayerControlViewID));
        }
        // process the image:
        //Todo: Do optimization and cache the url of image in here to avoid the waste in API calls
        // We can use SQlite or SharedReference to locally cache the Urls (cache the UnsplashModel)

        if(unsplashImgModel.getRemainedNumber() == 0){
            Log.d("UnsplashImgModel_0", "onViewCreated: ONLY ONE ");
            binding.ProgressBarID.setVisibility(View.VISIBLE);
            new UnsplashAsynctask(unsplashImgAdapter,unsplashImgModel,ImgQuery,page).execute();
        }
        else{
            Log.d("UnsplashImgModel_0", "onViewCreated: SECOND TWO ");
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
        // stop working on the background to avoid the waste in used resource.
      //  soundCloudExecutor.StopProcess();
    }
}
