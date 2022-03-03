package com.example.chillody.Adapter;

import android.app.Application;
import android.content.Context;
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
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.example.chillody.R;
import com.google.android.exoplayer2.MediaItem;

import java.util.List;

public class FavSongAdapter extends RecyclerView.Adapter<FavSongAdapter.FavSongViewHolder> {
    private List<FavoriteYoutubeElement> favoriteYoutubeElements;
    private final LayoutInflater layoutInflater;
    private final SingletonExoPlayer singletonExoPlayer;
    private FavoriteYoutubeViewModel favoriteYoutubeViewModel;
    public FavSongAdapter(Context context, Application application, FavoriteYoutubeViewModel model){
        layoutInflater = LayoutInflater.from(context);
        favoriteYoutubeElements = null;
        singletonExoPlayer = SingletonExoPlayer.getInstance(application);
        favoriteYoutubeViewModel = model;
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
        private final ImageView PlayIMG, TrashIMG;
        private MediaItem mediaItem;
        public FavSongViewHolder(@NonNull View itemView) {
            super(itemView);
            SongTitle = itemView.findViewById(R.id.nameItemID);
            PlayIMG = itemView.findViewById(R.id.PlayID);
            TrashIMG = itemView.findViewById(R.id.trashID);
            PlayIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!favoriteYoutubeElements.get(getLayoutPosition()).getType().equals(singletonExoPlayer.getType()))
                    {
                        singletonExoPlayer.setType(favoriteYoutubeElements.get(getLayoutPosition()).getType());
                        singletonExoPlayer.EndMusic();
                        AddSongsIntoExoPlayer();
                    }
                    else if(singletonExoPlayer.getExoPlayer().getMediaItemCount() != favoriteYoutubeElements.size())
                        AddSongsIntoExoPlayer();
                }
            });
            TrashIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(favoriteYoutubeElements.size() != 0)
                    {
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
                mediaItem = new MediaItem.Builder()
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
