package com.example.chillody.Networking;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// this is to update the Music Quote and its author for the layout.home layout fragment
public class MusicQuoteAsyncTask extends AsyncTask<Void,Void,String> {
    private final WeakReference<TextView> musicQuoteView;
    private final WeakReference<TextView> authormusicQuoteView;
    private String artist,quote;
    public MusicQuoteAsyncTask(TextView musicView, TextView authorView,String artist, String quote){
        this.musicQuoteView = new WeakReference<>(musicView);
        this.authormusicQuoteView = new WeakReference<>(authorView);
        this.artist = artist;
        this.quote = quote;
    }
    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"key1\": \"value\",\r\n    \"key2\": \"value\"\r\n}");
        Request request = new Request.Builder()
                .url("https://motivational-quotes1.p.rapidapi.com/motivation")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("X-RapidAPI-Host", "motivational-quotes1.p.rapidapi.com")
                .addHeader("X-RapidAPI-Key", "2347dba099msh0ada479f8c42f9dp144201jsn8fc8b09b17f7")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s==null){
            musicQuoteView.get().setText("");
            authormusicQuoteView.get().setText("");
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                musicQuoteView.get().setText(quote);
                authormusicQuoteView.get().setText(artist);
            }
        };
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                for(int i=1;i< s.length(); i++)
                {
                    if(s.charAt(i) == '"'){
                        quote = s.substring(1,i-1);
                        artist = s.substring(i+1,s.length()-1);
                    }
                }
                Message message = new Message();
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}
