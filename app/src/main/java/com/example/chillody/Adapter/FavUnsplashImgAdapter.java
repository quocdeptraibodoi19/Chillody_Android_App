package com.example.chillody.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.chillody.Model.UnsplashImgElement;
import com.example.chillody.R;

import java.util.List;

// This adapter has many convenient features just like RecyclerView.Adapter ( they also have notifydatasetchange() .... you can explore more in the ctrl + o)
public class FavUnsplashImgAdapter extends ArrayAdapter<UnsplashImgElement> {
    private final List<UnsplashImgElement> LovingUnsplashImgList;
    private final int LayoutItemId;

    public FavUnsplashImgAdapter(@NonNull Context context, int resource, @NonNull List<UnsplashImgElement> objects) {
        super(context, resource, objects);
        LayoutItemId = resource;
        LovingUnsplashImgList = objects;
    }

    // This is to define the number of elements shown on the screen
    @Override
    public int getCount() {
        return LovingUnsplashImgList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(LayoutItemId,null);
        }
        ImageView unsplashImg = convertView.findViewById(R.id.FavUnsplashImgID);
        TextView authorTextView = convertView.findViewById(R.id.authorID);
        UnsplashImgElement element = LovingUnsplashImgList.get(position);
        Glide.with(getContext())
                .load(element.getRegularURL())
                .centerCrop()
                .into(unsplashImg);
        authorTextView.setText(element.getArtistName());
        return convertView;
    }
}
