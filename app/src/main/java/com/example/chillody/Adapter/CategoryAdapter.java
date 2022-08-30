package com.example.chillody.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chillody.Activity_Fragment.home_fragment;
import com.example.chillody.Model.categoryObj;
import com.example.chillody.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<categoryObj> categoryObjList = new ArrayList<>();
    private home_fragment  home_fragment;
    private Context context;
    private LayoutInflater inflater;
    public CategoryAdapter(Context context, home_fragment fragment){
        inflater = LayoutInflater.from(context);
        this.home_fragment = fragment;
        this.context = context;
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cattegory_view,parent,false);
        return new CategoryViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.button.setText(categoryObjList.get(position).getName());
//        Picasso.get().load(categoryObjList.get(position).getUrl())
//                .into(holder.imageView);
        Glide.with(context).load(categoryObjList.get(position).getUrl())
                .centerCrop()
                .into(holder.imageView);
    }
    public void setCategoryObjList(List<categoryObj> objList){
        this.categoryObjList = objList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(categoryObjList == null) return 0;
        return categoryObjList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{
        private MaterialButton button;
        private ImageView imageView;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.catte_buttonID);
            imageView = itemView.findViewById(R.id.overlayID);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("NameCategory",categoryObjList.get(getLayoutPosition()).getName());
                    NavHostFragment.findNavController(home_fragment).navigate(R.id.action_home_fragment_to_music_fragment,bundle);
                }
            });

        }
    }
}
