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

import com.example.chillody.Model.FavoriteYoutubeElement;
import com.example.chillody.Model.FavoriteYoutubeViewModel;
import com.example.chillody.Model.GeneralYoutubeViewModel;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.example.chillody.R;
import com.google.android.exoplayer2.MediaItem;

import org.xml.sax.helpers.NamespaceSupport;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavSongAdapter extends RecyclerView.Adapter<FavSongAdapter.FavSongViewHolder> {
    private List<FavoriteYoutubeElement> favoriteYoutubeElements;
    private final LayoutInflater layoutInflater;
    private final SingletonExoPlayer singletonExoPlayer;
    private final FavoriteYoutubeViewModel favoriteYoutubeViewModel;
    private final GeneralYoutubeViewModel generalYoutubeViewModel;
    private final TextView TitleTrack;
    public FavSongAdapter(Context context, Application application, FavoriteYoutubeViewModel model, GeneralYoutubeViewModel generalYoutubeViewModel,TextView NameSong){
        layoutInflater = LayoutInflater.from(context);
        favoriteYoutubeElements = null;
        singletonExoPlayer = SingletonExoPlayer.getInstance(application);
        favoriteYoutubeViewModel = model;
        this.generalYoutubeViewModel = generalYoutubeViewModel;
        this.TitleTrack = NameSong;
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
        }

    }

    public void setCurrentList(List<FavoriteYoutubeElement> elementList){
        Log.d("QuocBug", "setCurrentList: ignite");
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

         public FavSongViewHolder(@NonNull View itemView) {
            super(itemView);
            SongTitle = itemView.findViewById(R.id.nameItemID);
             ImageView playIMG = itemView.findViewById(R.id.PlayID);
             ImageView trashIMG = itemView.findViewById(R.id.trashID);
            playIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // this is the case in which from other type we navigate to the loving music list
                    if(!favoriteYoutubeElements.get(getLayoutPosition()).getType().equals(singletonExoPlayer.getType()))
                    {
                        Log.d("MusicElement", "onClick: if 1");
                        singletonExoPlayer.setType(favoriteYoutubeElements.get(getLayoutPosition()).getType());
                        singletonExoPlayer.EndMusic();
                        AddSongsIntoExoPlayer();
                    }
                    // this is when the list in the singleton is matching with the real list in the loving fragment
                    else{
                        Log.d("MusicElement", "onClick: if 3");
                        singletonExoPlayer.getExoPlayer().seekTo(getLayoutPosition());
                        singletonExoPlayer.getExoPlayer().prepare();
                        singletonExoPlayer.getExoPlayer().play();
                    }
                    TitleTrack.setText(favoriteYoutubeElements.get(getLayoutPosition()).getTitle());
                }
            });
            trashIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(favoriteYoutubeElements.size() != 0)
                    {
                        generalYoutubeViewModel.updateDislikeMusicElement(favoriteYoutubeElements.get(getLayoutPosition()).getMusicID());
                        if(singletonExoPlayer.getExoPlayer().getMediaItemCount() != 0)
                        {
                            if(favoriteYoutubeElements.get(getLayoutPosition()).getType().contains(singletonExoPlayer.getType())){
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
                                            if(element.getMusicID().equals(favoriteYoutubeElements.get(getLayoutPosition()).getMusicID()))
                                            {
                                                // this can update UI of the music bar
                                                // because owing to some magical logics, element bellow still reference to the object
                                                // store in the localConfiguration.tag.
                                                element.setFavorite(false);
                                                singletonExoPlayer.getExoPlayer().removeMediaItem(i);
                                                break;
                                            }
                                        }
                                }
                            }
                        }
                        favoriteYoutubeViewModel.DeleteSongElements(favoriteYoutubeElements.get(getLayoutPosition()));
                        favoriteYoutubeElements.remove(getLayoutPosition());
                        notifyDataSetChanged();
                    }
                }
            });
        }
        private void AddSongsIntoExoPlayer(){
            if(singletonExoPlayer.getExoPlayer().getMediaItemCount()!=0)
                singletonExoPlayer.EndMusic();
            for(int i=0;i<favoriteYoutubeElements.size();i++){
                MediaItem mediaItem = new MediaItem.Builder()
                        .setUri(favoriteYoutubeElements.get(i).getDownloadedMusicUrl())
                        .setTag(favoriteYoutubeElements.get(i).getYoutubeMusicElement())
                        .build();
                singletonExoPlayer.getExoPlayer().addMediaItem(mediaItem);
                if(i == getLayoutPosition())
                {
                    singletonExoPlayer.getExoPlayer().prepare();
                    singletonExoPlayer.getExoPlayer().seekTo(i);
                    singletonExoPlayer.getExoPlayer().play();
                }
            }
        }
    }
}
