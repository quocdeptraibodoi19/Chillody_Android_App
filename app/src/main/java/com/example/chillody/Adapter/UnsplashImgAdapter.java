package com.example.chillody.Adapter;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chillody.Model.DoubleClickListener;
import com.example.chillody.Model.FavoriteUnsplashImgViewModel;
import com.example.chillody.Model.UnsplashImgElement;
import com.example.chillody.R;

import java.lang.ref.WeakReference;

public class UnsplashImgAdapter extends RecyclerView.Adapter<UnsplashImgAdapter.UnsplashImgViewHolder> {
    private WeakReference<ProgressBar> progressBarWeakReference;
    private UnsplashImgElement element;
    private LayoutInflater inflater;
    private Context context;
    private FavoriteUnsplashImgViewModel favoriteUnsplashImgViewModel;
    public UnsplashImgAdapter(Context context,ProgressBar progressBar,FavoriteUnsplashImgViewModel favoriteUnsplashImgViewModel){
        inflater = LayoutInflater.from(context);
        this.context = context;
        progressBarWeakReference = new WeakReference<>(progressBar);
        this.favoriteUnsplashImgViewModel = favoriteUnsplashImgViewModel;
    }
    @NonNull
    @Override
    public UnsplashImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.image_element_layout,parent,false);
        return new UnsplashImgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnsplashImgViewHolder holder, int position) {
        // this is just for a while until finishing the Favorite section Logic:
        holder.WhiteHeartIcon.setVisibility(View.INVISIBLE);
        if(element != null){
            Glide.with(context)
                    .load(element.getRegularURL())
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBarWeakReference.get().setVisibility(View.INVISIBLE);
                            if(element.getIsFavorite() == 1){
                                holder.WhiteHeartIcon.setVisibility(View.GONE);
                                holder.RedHeartIcon.setVisibility(View.VISIBLE);
                            }
                            else{
                                holder.WhiteHeartIcon.setVisibility(View.VISIBLE);
                                holder.RedHeartIcon.setVisibility(View.GONE);

                            }
                            holder.UnsplashImage.setOnClickListener(new DoubleClickListener() {
                                @Override
                                public void onDoubleClick(View v) {
                                    Log.d("Ah", "onDoubleClick: Double animation");
                                    holder.InsHeartIcon.setVisibility(View.VISIBLE);
                                    holder.InsHeartIcon.setAlpha(0.75f);
                                    if(holder.BiggestLovingdrawable instanceof AnimatedVectorDrawable){
                                        Log.d("Ah", "onDoubleClick: ani");
                                        holder.BiggestLovingImageAnimatedDrawable = (AnimatedVectorDrawable) holder.BiggestLovingdrawable;
                                        holder.BiggestLovingImageAnimatedDrawable.start();
                                        holder.WhiteHeartIcon.setVisibility(View.GONE);
                                        holder.RedHeartIcon.setVisibility(View.VISIBLE);
                                        holder.SmallLovingImageAnimatedDrawable = (AnimatedVectorDrawable) holder.RegularLovingdrawable;
                                        holder.SmallLovingImageAnimatedDrawable.start();
                                        if(element.getIsFavorite() == 0){
                                            element.setIsFavorite(1);
                                            favoriteUnsplashImgViewModel.updateFavoriteUnsplashImg(element);
                                        }
                                    }
                                }
                            });
                            // this is to add effect and logic for the small heart icon on the top-right corner
                            holder.WhiteHeartIcon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.WhiteHeartIcon.setVisibility(View.GONE);
                                    holder.RedHeartIcon.setVisibility(View.VISIBLE);
                                    element.setIsFavorite(1);
                                    favoriteUnsplashImgViewModel.updateFavoriteUnsplashImg(element);
                                }
                            });
                            holder.RedHeartIcon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.WhiteHeartIcon.setVisibility(View.VISIBLE);
                                    holder.RedHeartIcon.setVisibility(View.GONE);
                                    holder.SmallLovingImageAnimatedDrawable = (AnimatedVectorDrawable) holder.RegularLovingdrawable;
                                    holder.SmallLovingImageAnimatedDrawable.start();
                                    element.setIsFavorite(0);
                                    favoriteUnsplashImgViewModel.updateFavoriteUnsplashImg(element);
                                }
                            });
                            return false;
                        }
                    })
                    .into(holder.UnsplashImage);
        }
    }

    @Override
    public int getItemCount() {
        if(element != null) return 1;
        return 0;
    }
    public void setElement(UnsplashImgElement element){
        this.element = element;
        notifyDataSetChanged();
    }
    protected class UnsplashImgViewHolder extends RecyclerView.ViewHolder{
        private final ImageView UnsplashImage;
        private final ImageView WhiteHeartIcon;
        private final ImageView RedHeartIcon;
        private final ImageView InsHeartIcon;
        // using the Animated Vector Drawable to add animation to the icon
        private AnimatedVectorDrawable BiggestLovingImageAnimatedDrawable;
        private AnimatedVectorDrawable SmallLovingImageAnimatedDrawable;
        private final Drawable BiggestLovingdrawable;
        private final Drawable RegularLovingdrawable;
        public UnsplashImgViewHolder(@NonNull View itemView) {
            super(itemView);
            UnsplashImage = itemView.findViewById(R.id.unsplashIMGID);
            WhiteHeartIcon = itemView.findViewById(R.id.WhiteHeartIconID);
            RedHeartIcon = itemView.findViewById(R.id.RedHeartIconID);
            InsHeartIcon = itemView.findViewById(R.id.InsHeartIcon);
            BiggestLovingdrawable = InsHeartIcon.getDrawable();
            RegularLovingdrawable = RedHeartIcon.getDrawable();
        }
    }
}
