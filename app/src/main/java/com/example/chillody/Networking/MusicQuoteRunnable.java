package com.example.chillody.Networking;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// this is to update the Music Quote and its author for the layout.home layout fragment
public class MusicQuoteRunnable extends AsyncTask<Void,Void,String> {
    private final WeakReference<TextView> musicQuoteView;
    private final WeakReference<TextView> authormusicQuoteView;
    private ProgressDialog dialog;
    private  Context context;
    public MusicQuoteRunnable(TextView musicView, TextView authorView, Context context){
        this.musicQuoteView = new WeakReference<>(musicView);
        this.authormusicQuoteView = new WeakReference<>(authorView);
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog =new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://metal-music-quotes.p.rapidapi.com/quotes")
                .get()
                .addHeader("x-rapidapi-host", "metal-music-quotes.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MusicQuoteRunnable", "doInBackground: Something related to response fails");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dialog.dismiss();
        String artist = "- ";
        try {
            JSONObject data = new JSONObject(s);
             artist += data.getJSONObject("data").getString("artist");
            String quote = data.getJSONObject("data").getString("quote");
            musicQuoteView.get().setText(quote);
            authormusicQuoteView.get().setText(artist);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
