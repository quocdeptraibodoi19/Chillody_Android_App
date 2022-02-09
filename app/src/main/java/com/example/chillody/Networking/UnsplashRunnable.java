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

public class UnsplashRunnable extends AsyncTask<Void,Void,String> {
    private com.example.chillody.Model.UnsplashImgModel UnsplashImgModel;
    private UnsplashImgAdapter unsplashImgAdapter;
    private String Query = "query=";
    private String Page = "page=";
    private WeakReference<ProgressBar> progressBarWeakReference;
    public UnsplashRunnable(ProgressBar progressBar, UnsplashImgAdapter adapter, UnsplashImgModel model, String Query, String Page){
        this.UnsplashImgModel = model;
        this.Query += Query;
        this.Page += Page;
        this.unsplashImgAdapter = adapter;
        progressBarWeakReference = new WeakReference<>(progressBar);
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
            Log.d("UnsplashResponse", "doInBackground: Body: "+s);
            JSONObject object = new JSONObject(s);
            JSONArray jsonArray = object.getJSONArray("results");
            Log.d("UnsplashNumber", "onPostExecute: "+ String.valueOf(jsonArray.length()));
            for(int i =0 ; i< jsonArray.length();i++){
                UnsplashImgModel.addElement(new UnsplashImgElement(jsonArray.getJSONObject(i)));
            }
            unsplashImgAdapter.setElement(UnsplashImgModel.getCurrentElement());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
