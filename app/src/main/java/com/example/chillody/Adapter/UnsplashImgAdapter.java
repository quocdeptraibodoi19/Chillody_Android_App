package com.example.chillody.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.example.chillody.Model.UnsplashImgElement;
import com.example.chillody.R;
import com.github.ybq.android.spinkit.style.FoldingCube;

import java.lang.ref.WeakReference;

public class UnsplashImgAdapter extends RecyclerView.Adapter<UnsplashImgAdapter.UnsplashImgViewHolder> {
    private WeakReference<ProgressBar> progressBarWeakReference;
    private UnsplashImgElement element;
    private LayoutInflater inflater;
    private Context context;
    public UnsplashImgAdapter(Context context,ProgressBar progressBar){
        inflater = LayoutInflater.from(context);
        this.context = context;
        progressBarWeakReference = new WeakReference<>(progressBar);
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
                            holder.WhiteHeartIcon.setVisibility(View.VISIBLE);
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
        private ImageView RedHeartIcon;
        public UnsplashImgViewHolder(@NonNull View itemView) {
            super(itemView);
            UnsplashImage = itemView.findViewById(R.id.unsplashIMGID);
            WhiteHeartIcon = itemView.findViewById(R.id.WhiteHeartIconID);
            RedHeartIcon = itemView.findViewById(R.id.RedHeartIconID);
        }
    }
}
