package com.example.chillody.Networking;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.chillody.Model.FavSongRepository;
import com.example.chillody.Model.FavoriteYoutubeElement;
import com.example.chillody.Model.GeneralSongRepository;
import com.example.chillody.Model.GeneralYoutubeViewModel;
import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
* Simple YouTube Search
* Youtube Search
* YouTube MP3
* https://rapidapi.com/ytjar/api/youtube-mp36/
* */
//ToDo: Have to figure out the reason why the ControlView live when we backstack to access another category (the ExoPlayer live but there are no reasons why controlView lives)
// Maybe because of using AndroidViewModel?
// => In fact, when the configuration changes occur, the ControlView do be deleted =)) (I know the grammar is wrong but it's used to emphasize =)) )
// When you backstack and open another category, the YoutubeExecutor is invoked again and it add songs to the exoplayer.
// the exoplayer is singleton and it will live through the program.
// and it can not play song because of the if condition ( if (!exoplauer.isplaying()....)).
// Therefore, it is so amazing when applying singleton design pattern for the exoplayer in this case.
public class YoutubeExecutor  {
    private final static int EXE_SONG_MESSAGE = 4124456;
    private final static int EXE_RECOMMEND_SONG_MESSAGE = 11012031;
    private final static int EXE_FAILURE_SONG_MESSAGE = 1120231247;
    private final SingletonExoPlayer singletonExoPlayer;
    private ExecutorService executorService;
    private final GeneralSongRepository repository;
    private final FavSongRepository favSongRepository;
    public YoutubeExecutor(@NonNull Application application) {
        singletonExoPlayer = SingletonExoPlayer.getInstance(application);
        repository =  new GeneralSongRepository(application);
        favSongRepository = new FavSongRepository(application);
    }
    public void MusicAsyncExecutor(String musicType,String query, WeakReference<TextView>NextSongTitle){
        singletonExoPlayer.setThreadProcessing(true);
        executorService = Executors.newFixedThreadPool(1);
        int lastUpdate = singletonExoPlayer.getExoPlayer().getMediaItemCount();
        final YoutubeMusicElement[] element = new YoutubeMusicElement[1];
        ExoPlayer exoPlayer = singletonExoPlayer.getExoPlayer();
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == EXE_SONG_MESSAGE){
                    element[0] = (YoutubeMusicElement) msg.obj;
                  //  MediaItem item = MediaItem.fromUri(youtubeMusicModelWeakReference.get().getMusicElement(msg.arg1).getDownloadedMusicUrl());
                    MediaItem item = new MediaItem.Builder().setUri(element[0].getDownloadedMusicUrl())
                            .setTag(element[0]).build();
                    exoPlayer.addMediaItem(item);
                    repository.insertNewSong(element[0]);
                    Log.d("QuocPhu", "handleMessage: Flag: "+ String.valueOf(singletonExoPlayer.getUIUpdatingFlag()) +" current pos: "+String.valueOf(msg.arg1));
                    if(singletonExoPlayer.getUIUpdatingFlag()!=-1 && singletonExoPlayer.getUIUpdatingFlag() == msg.arg1)
                        {
                           // Log.d("QuocPhu", "handleMessage: Flag: "+ String.valueOf(singletonExoPlayer.getUIUpdatingFlag()) +" current pos: "+String.valueOf(singletonExoPlayer.getExoPlayer().getCurrentMediaItemIndex()));
                            Log.d("QuocPhu", "handleMessage: Update the name of next song: "+ element[0].getTitle());
                            singletonExoPlayer.setUIUpdatingFlag(-1);
                            if(NextSongTitle != null)
                                NextSongTitle.get().setText(element[0].getTitle());
                        }
                    if(msg.arg1 == lastUpdate)
                    {
                        exoPlayer.prepare();
                        exoPlayer.play();
                    }
                }
            }
        };
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // This bellow for getting the songs' information
                Log.d("YouBug", "run: song search");
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()                                  // Because the query of this api need being encoded first
                        .url("https://simple-youtube-search.p.rapidapi.com/search?query="+new String(query.getBytes(StandardCharsets.UTF_8))+"&type=video&safesearch=true")
                        .get()
                        .addHeader("x-rapidapi-host", "simple-youtube-search.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    // Processing the Json from the response body above
                    JSONArray searchedSongsArray = new JSONObject(Objects.requireNonNull(response.body()).string()).getJSONArray("results");
                    String title;
                    String songId;
                    String songUrl;
                    // Todo: Be quick to do the optimization and update the i<2 (we set i<2 in order to save the resource)
                    for(int i=lastUpdate; i< lastUpdate+2; i++){
                        title = searchedSongsArray.getJSONObject(i - lastUpdate).getString("title");
                        Log.d("YouBug", "run: "+ title);
                        songId = searchedSongsArray.getJSONObject(i - lastUpdate).getString("id");
                        Log.d("YouBug", "run: "+ songId);
                        // To get the mp3 form from the ID of the youtube ID
                        client = new OkHttpClient();
                        request = new Request.Builder()
                                .url("https://youtube-mp36.p.rapidapi.com/dl?id="+songId)
                                .get()
                                .addHeader("x-rapidapi-host", "youtube-mp36.p.rapidapi.com")
                                .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                                .build();
                        response = client.newCall(request).execute();
                        songUrl = new JSONObject(Objects.requireNonNull(response.body()).string()).getString("link");
                        Log.d("YouBug", "run: bug in the message");
                         Message message = new Message();
                         message.what = EXE_SONG_MESSAGE;
                         message.arg1 = i;
                         message.obj = new YoutubeMusicElement(title,songId,songUrl,musicType,0);
                         handler.sendMessage(message);
                    }
                    singletonExoPlayer.setThreadProcessing(false);
                } catch (IOException | JSONException  e) {
                    e.printStackTrace();
                    // for the backing up
                    singletonExoPlayer.setThreadProcessing(false);
                }
            }
        });
        executorService.shutdown();
        executorService = null;
    }
    public void MusicRecommendingExecutor(String musicType,String lastSongID, WeakReference<TextView>NextSongTitle){
        if(singletonExoPlayer.isThreadProcessing() || isExecuting()) return;
        Log.d("QuocMusic", "MusicRecommendingExecutor: Recommendation processing");
        executorService = Executors.newFixedThreadPool(1);
        singletonExoPlayer.setThreadProcessing(true);
        ExoPlayer exoPlayer = singletonExoPlayer.getExoPlayer();
        int lastIndexOfSong = exoPlayer.getMediaItemCount();
        final YoutubeMusicElement[] element = new YoutubeMusicElement[1];
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                // bellow code is different from the above one because this function can ignite at the home_fragment whereas the youtubeMusicModelWeakReference can be null
                if(msg.what == EXE_RECOMMEND_SONG_MESSAGE){
                    element[0] = (YoutubeMusicElement) msg.obj;
                    MediaItem item = new MediaItem.Builder().setUri(element[0].getDownloadedMusicUrl())
                            .setTag(element[0]).build();
                    exoPlayer.addMediaItem(item);
                    if(singletonExoPlayer.getUIUpdatingFlag()!= -1 && singletonExoPlayer.getUIUpdatingFlag() == exoPlayer.getMediaItemCount()-1)
                        {
                            singletonExoPlayer.setUIUpdatingFlag(-1);
                            if(NextSongTitle != null)
                                NextSongTitle.get().setText(element[0].getTitle());
                        }
                    repository.insertNewSong(element[0]);
                    exoPlayer.prepare();
                }
            }
        };
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("QuocMusic", "run: in the executor: "+ String.valueOf(lastIndexOfSong));
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://youtube-search6.p.rapidapi.com/video/recommendation/?videoId="+lastSongID)
                        .get()
                        .addHeader("x-rapidapi-host", "youtube-search6.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    JSONArray SongArray = new JSONObject(Objects.requireNonNull(response.body()).string()).getJSONArray("videos");
                    String title;
                    String songId;
                    String songUrl;
                    Bundle bundle = new Bundle();
                    for(int i=lastIndexOfSong;i<lastIndexOfSong+2;i++){
                        title = SongArray.getJSONObject(i - lastIndexOfSong).getString("title");
                        songId = SongArray.getJSONObject(i - lastIndexOfSong).getString("video_id");
                        // To get the mp3 form from the ID of the youtube ID
                        Log.d("QuocMusic", "run: the title is: "+ title);
                        client = new OkHttpClient();
                        request = new Request.Builder()
                                .url("https://youtube-mp36.p.rapidapi.com/dl?id="+songId)
                                .get()
                                .addHeader("x-rapidapi-host", "youtube-mp36.p.rapidapi.com")
                                .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                                .build();
                        response = client.newCall(request).execute();
                        songUrl = new JSONObject(Objects.requireNonNull(response.body()).string()).getString("link");
                        Message message = new Message();
                        message.what = EXE_RECOMMEND_SONG_MESSAGE;
                        message.obj = new YoutubeMusicElement(title,songId,songUrl,musicType,0);
                        handler.sendMessage(message);
                    }
                    singletonExoPlayer.setThreadProcessing(false);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    singletonExoPlayer.setThreadProcessing(false);
                }
            }
        });
        executorService.shutdown();
        executorService = null;
    }
    public boolean isExecuting(){
        return executorService != null;
    }

    // if failure happens, this will ignite in order to reload the url.
    public void failHandlingSong(String ID, int position){
        Log.d("Trong", "failHandlingSong: in the process of correcting the link");
        if(singletonExoPlayer.isThreadProcessing() || isExecuting()) return;
        singletonExoPlayer.setThreadProcessing(true);
        ExoPlayer player = singletonExoPlayer.getExoPlayer();
        ExecutorService service = Executors.newFixedThreadPool(1);
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == EXE_FAILURE_SONG_MESSAGE){
                    // because you can't change the path of the uri that is available in the list
                    // the only way you can change the path of the uri is to delete it and create a new one
                    if(player.getMediaItemAt(position).localConfiguration != null){
                       YoutubeMusicElement element = (YoutubeMusicElement) Objects.requireNonNull(player.getMediaItemAt(position).localConfiguration).tag;
                       // even though you change the DownLoadedMusicUrl, it just change the tag in the localConfiguration.
                        // the key element that helps you to listen the music is the Uri in the localConfiguration.
                        // But as mentioned above, the Uri cannot be changed because it does not have any set methods ( so stupid :) )
                        // therefore, the only way you can do is to remove the current one and substitute it with the new instance of that object.
                       element.setDownloadedMusicUrl((String) msg.obj);
                        // why you have to add it at position +1 because this function will add the new object at that index +1 ( it means that the new added item' position will lie in that position +1 )
                       player.addMediaItem(position+1,new MediaItem.Builder().setUri(element.getDownloadedMusicUrl()).setTag(element).build());
                       // you remove the current old one :) .... so stupid. I don't understand why they do not provide the set method for the Uri object :).
                        player.removeMediaItem(position);
                       // This is for the favorite loving music is stored in another table... therefore, we have to check for the suitable database.
                        singletonExoPlayer.setErrorProcessedFlag(true);
                        // if you simply insert song into repository like this ... the order may not remain the same ( the new song even though it replaces the old one because of your defined way to resolve the conflict.... but the order may not remain the same... the new one will be added into the rear of the list)
                        // I mean in the past you used to use the function: repository.insertNewSong(element)
                        if(!element.getMusicType().contains("Love"))
                            repository.updateDownLoadUrl(element.getMusicID(),element.getDownloadedMusicUrl());
                       else
                           favSongRepository.InsertSongElement(new FavoriteYoutubeElement(element.getMusicID(),(String) msg.obj,element.getTitle(), element.getMusicType()));
                       player.prepare();
                       // because there is just one instance of message.Therefore, we can mark the finish flag here.
                        singletonExoPlayer.setThreadProcessing(false);
                    }
                }
            }
        };
        service.execute(new Runnable() {
            @Override
            public void run() {
                // get mp3 from ID
                singletonExoPlayer.setThreadProcessing(true);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://youtube-mp36.p.rapidapi.com/dl?id="+ID)
                        .get()
                        .addHeader("x-rapidapi-host", "youtube-mp36.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                        .build();

                try {
                    // this is done synchronously ( note that synchronous process must be done in the worker thread).
                    Response response = client.newCall(request).execute();
                    String body = response.body().string();
                    Log.d("Trong", "failHandlingSong: the content of json: "+ body);
                    String songUrl = new JSONObject(body).getString("link");
                    Message message = new Message();
                    message.what = EXE_FAILURE_SONG_MESSAGE;
                    message.obj = songUrl;
                    handler.sendMessage(message);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
