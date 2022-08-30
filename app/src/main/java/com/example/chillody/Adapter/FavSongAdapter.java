package com.example.chillody.Adapter;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillody.Model.FavoriteRecyclerViewManager;
import com.example.chillody.Model.FavoriteYoutubeElement;
import com.example.chillody.Model.FavoriteYoutubeViewModel;
import com.example.chillody.Model.GeneralYoutubeViewModel;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.example.chillody.R;
import com.google.android.exoplayer2.MediaItem;

import org.xml.sax.helpers.NamespaceSupport;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavSongAdapter extends RecyclerView.Adapter<FavSongAdapter.FavSongViewHolder> {
    private List<FavoriteYoutubeElement> favoriteYoutubeElements;
    private final LayoutInflater layoutInflater;
    private final SingletonExoPlayer singletonExoPlayer;
    private final FavoriteYoutubeViewModel favoriteYoutubeViewModel;
    private final GeneralYoutubeViewModel generalYoutubeViewModel;
    private final TextView TitleTrack;
    private final ImageView whiteBtn, redBtn;
    private final FavoriteRecyclerViewManager favoriteRecyclerViewManager;
    public FavSongAdapter(Context context, Application application, FavoriteYoutubeViewModel model, GeneralYoutubeViewModel generalYoutubeViewModel,TextView NameSong,ImageView whiteBtn, ImageView redBtn,FavoriteRecyclerViewManager manager){
        layoutInflater = LayoutInflater.from(context);
        favoriteYoutubeElements = null;
        singletonExoPlayer = SingletonExoPlayer.getInstance(application);
        favoriteYoutubeViewModel = model;
        this.generalYoutubeViewModel = generalYoutubeViewModel;
        this.TitleTrack = NameSong;
        this.whiteBtn = whiteBtn;
        this.redBtn = redBtn;
        this.favoriteRecyclerViewManager = manager;
    }
    @NonNull
    @Override
    public FavSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.fav_music_item,parent,false);
        return new FavSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavSongViewHolder holder, int position) {
        Log.d("QuocBug", "onBindViewHolder: Ignite the Adapter");
        if(favoriteYoutubeElements!= null)
        {
            Log.d("QuocBug", "onBindViewHolder: Inside the tag: "+ favoriteYoutubeElements.size());
            Log.d("QuocBug", "onBindViewHolder: title is : " +favoriteYoutubeElements.get(position).getTitle() );
            Log.d("QuocBug", "onBindViewHolder: position: "+String.valueOf(position));
            holder.SongTitle.setText(favoriteYoutubeElements.get(position).getTitle());
            holder.playIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(singletonExoPlayer.getExoPlayer().getCurrentMediaItem()!=null && singletonExoPlayer.getExoPlayer().getCurrentMediaItem().localConfiguration!=null){
                         if(((YoutubeMusicElement) singletonExoPlayer.getExoPlayer().getCurrentMediaItem().localConfiguration.tag).getMusicID().equals(favoriteYoutubeElements.get(holder.getLayoutPosition()).getMusicID()) && singletonExoPlayer.getType().contains("Love")){
                            singletonExoPlayer.getExoPlayer().play();
                             TitleTrack.setText(favoriteYoutubeElements.get(holder.getLayoutPosition()).getTitle());
                             Log.d("stress", "onClick: comebacking loving");
                             return;
                        }
                    }
                    Log.d("blackpink", "onClick: the type of an exoplayer: "+ String.valueOf(singletonExoPlayer.getType()));
                    Log.d("blackpink", "onClick: the type of selected item: "+ String.valueOf(favoriteYoutubeElements.get(holder.getLayoutPosition()).getType()));
                    Log.d("blackpink", "onClick: the type of manager: "+ String.valueOf(favoriteRecyclerViewManager.getType()));
                    if(singletonExoPlayer.getType().equals(favoriteYoutubeElements.get(holder.getLayoutPosition()).getType()) && holder.getLayoutPosition() == favoriteRecyclerViewManager.getPosition()) {
                        Log.d("blackpink", "onClick: if: 1");
                        singletonExoPlayer.getExoPlayer().play();
                    }
                    else if(!favoriteYoutubeElements.get(holder.getLayoutPosition()).getType().equals(singletonExoPlayer.getType()))
                    {
                        Log.d("blackpink", "onClick: if: 2");
                        Log.d("MusicElement", "onClick: if 1");
                        Log.d("MusicElement", "onClick: real position: "+ String.valueOf(holder.getLayoutPosition()));
                        singletonExoPlayer.EndMusic();
                        singletonExoPlayer.setType(favoriteYoutubeElements.get(holder.getLayoutPosition()).getType());
                        for(int i=0;i<favoriteYoutubeElements.size();i++){
                            MediaItem mediaItem = new MediaItem.Builder()
                                    .setUri(favoriteYoutubeElements.get(i).getDownloadedMusicUrl())
                                    .setTag(favoriteYoutubeElements.get(i).getYoutubeMusicElement())
                                    .build();
                            singletonExoPlayer.getExoPlayer().addMediaItem(mediaItem);
                        }
                        // when using the seekto remember to add positionMs
                        Log.d("MusicElement", "onClick: i is: "+ String.valueOf(holder.getLayoutPosition()));
                        singletonExoPlayer.getExoPlayer().pause();
                        singletonExoPlayer.getExoPlayer().prepare();
                        singletonExoPlayer.getExoPlayer().seekTo(holder.getLayoutPosition(),0);
                        singletonExoPlayer.getExoPlayer().play();
                    }
                    // this is when the list in the singleton is matching with the real list in the loving fragment
                    else{
                        Log.d("blackpink", "onClick: if: 3");
                        Log.d("MusicElement", "onClick: if "+ String.valueOf(holder.getLayoutPosition()));
                        singletonExoPlayer.getExoPlayer().pause();
                        singletonExoPlayer.getExoPlayer().seekTo(holder.getLayoutPosition(),0);
                        singletonExoPlayer.getExoPlayer().prepare();
                        singletonExoPlayer.getExoPlayer().play();
                    }
                    TitleTrack.setText(favoriteYoutubeElements.get(holder.getLayoutPosition()).getTitle());
                }
            });
            holder.pauseIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // this is the view of the current item
                    holder.pauseIMG.setVisibility(View.GONE);
                    holder.playIMG.setVisibility(View.VISIBLE);
                    singletonExoPlayer.getExoPlayer().pause();
                }
            });
            // the reason why when you click the trash icon to delete a song from the favorite list, the pause icon move immediately to the next one:
            // that because for example, the song you want to delete has the 2nd position, (currently the pause icon is visible)
            // when deleteing it, the 3rd song will turn into the 2nd song and pause icon will be visible
            holder.trashIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(favoriteYoutubeElements.size() != 0)
                    {
                        generalYoutubeViewModel.updateDislikeMusicElement(favoriteYoutubeElements.get(holder.getLayoutPosition()).getMusicID());
                        favoriteYoutubeViewModel.DeleteSongElements(favoriteYoutubeElements.get(holder.getLayoutPosition()));

                        if(singletonExoPlayer.getExoPlayer().getMediaItemCount() != 0)
                        {
                            if(favoriteYoutubeElements.get(holder.getLayoutPosition()).getType().contains(singletonExoPlayer.getType())){
                                // the newfixedthreadpool is the same thing with newsinglethread
                                // the reason why we do this here is because we want to propagate the checking process
                                // into another thread to avoid lowing down the users' experience
                                // however, the recent condition is to check in one loop with the size of array is not large
                                // therefore, we can implement this in the main thread.
                                YoutubeMusicElement element;
                                MediaItem item;
                                for(int i= singletonExoPlayer.getExoPlayer().getMediaItemCount()-1;i>=0;i--)
                                {
                                    item = singletonExoPlayer.getExoPlayer().getMediaItemAt(i);
                                    if(item.localConfiguration != null)
                                    {
                                        element = (YoutubeMusicElement) item.localConfiguration.tag;
                                        if(element.getMusicID().equals(favoriteYoutubeElements.get(holder.getLayoutPosition()).getMusicID()))
                                        {
                                            // this can update UI of the music bar
                                            // because owing to some magical logics, element bellow still reference to the object
                                            // store in the localConfiguration.tag.
                                            element.setFavorite(false);
                                            if(element.getMusicType().contains("Love"))
                                            {
                                                singletonExoPlayer.getExoPlayer().removeMediaItem(i);
                                                if(i == singletonExoPlayer.getExoPlayer().getMediaItemCount() && singletonExoPlayer.getExoPlayer().getMediaItemCount() != 0){
                                                    singletonExoPlayer.getExoPlayer().seekTo(singletonExoPlayer.getExoPlayer().getMediaItemCount()-1,0);
                                                    singletonExoPlayer.getExoPlayer().prepare();
                                                    singletonExoPlayer.getExoPlayer().play();
                                                }
                                                if(singletonExoPlayer.getType().equals(favoriteRecyclerViewManager.getType()) && i == favoriteRecyclerViewManager.getPosition()) {
                                                    favoriteRecyclerViewManager.setPosition(-1);
                                                }
                                                if(favoriteYoutubeElements.size() == 1) {
                                                    TitleTrack.setText(R.string.NoSong_Notification);
                                                    whiteBtn.setVisibility(View.VISIBLE);
                                                    redBtn.setVisibility(View.GONE);
                                                }
                                            }
                                            // this is not in the scope of loving music... thus, when clicking the trash, the song in the exoplayer is not cleared...
                                            // therefore, we need to update the UI...
                                            else{
                                                whiteBtn.setVisibility(View.VISIBLE);
                                                redBtn.setVisibility(View.GONE);
                                            }
//                                            favoriteYoutubeViewModel.DeleteSongElements(favoriteYoutubeElements.get(holder.getLayoutPosition()));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }

    }

    public void setCurrentList(List<FavoriteYoutubeElement> elementList){
        Log.d("QuocBug", "setCurrentList: ignite");
        if(favoriteYoutubeElements != null)
        Log.d("nika", "setCurrentList: the size of old one: "+ String.valueOf(favoriteYoutubeElements.size()));
        Log.d("nika", "setCurrentList: the size of the new one: "+ String.valueOf(elementList.size()));
        this.favoriteYoutubeElements = elementList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(favoriteYoutubeElements == null) return 0;
         return favoriteYoutubeElements.size();
    }
     class FavSongViewHolder extends RecyclerView.ViewHolder{
        private final TextView SongTitle;
        private final ImageView playIMG;
         private ImageView trashIMG;
         private ImageView pauseIMG;
         public FavSongViewHolder(@NonNull View itemView) {
            super(itemView);
            SongTitle = itemView.findViewById(R.id.nameItemID);
              playIMG = itemView.findViewById(R.id.PlayID);
              trashIMG = itemView.findViewById(R.id.trashID);
              pauseIMG = itemView.findViewById(R.id.PauseID);
        }
    }
}
