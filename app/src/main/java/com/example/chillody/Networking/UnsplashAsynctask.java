package com.example.chillody.Networking;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.chillody.Adapter.UnsplashImgAdapter;
import com.example.chillody.Model.UnsplashImgElement;
import com.example.chillody.Model.UnsplashImgModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
// Why you use AsyncTask here -> because the time it takes to get 10 urls of image from server
// is not that long. Therefore, the whole process may take around 1 - 2s.
// And because it's not that long. There are little chances for the memory leak to occur.
public class UnsplashAsynctask extends AsyncTask<Void,Void,String> {
    private final com.example.chillody.Model.UnsplashImgModel UnsplashImgModel;
    private final WeakReference<UnsplashImgAdapter> unsplashImgAdapter;
    private String Query = "query=";
    private String Page = "page=";
    public UnsplashAsynctask(UnsplashImgAdapter adapter, UnsplashImgModel model, String Query, String Page){
        this.UnsplashImgModel = model;
        this.Query += Query;
        this.Page += Page;
        this.unsplashImgAdapter = new WeakReference<>(adapter);
    }
    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.unsplash.com/search/photos?client_id=J12FKIpONAO8Bx-rINK3FxLl1jT0fRROxvhxr2FsxuM&per_page=100&";
        Request request = new Request.Builder().url(url+this.Query+"&"+this.Page).get().build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject object = new JSONObject(s);
            JSONArray jsonArray = object.getJSONArray("results");
            for(int i =0 ; i< jsonArray.length();i++){
                UnsplashImgModel.addElement(new UnsplashImgElement(jsonArray.getJSONObject(i)));
            }
            if(unsplashImgAdapter.get() != null)
            unsplashImgAdapter.get().setElement(UnsplashImgModel.getCurrentElement());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
