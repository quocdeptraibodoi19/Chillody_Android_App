package com.example.chillody.Networking;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.mbms.DownloadRequest;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.YoutubeMusicElement;
import com.example.chillody.Model.YoutubeMusicModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;

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

//ToDo: Have to figure out the reason why the ControlView live when we backstack to access another category (the ExoPlayer live but there are no reasons why controlView lives)
// Maybe because of using AndroidViewModel?
// => In fact, when the configuration changes occur, the ControlView do be deleted =)) (I know the grammar is wrong but it's used to emphasize =)) )
// When you backstack and open another category, the YoutubeExecutor is invoked again and it add songs to the exoplayer.
// the exoplayer is singleton and it will live through the program.
// and it can not play song because of the if condition ( if (!exoplauer.isplaying()....)).
// Therefore, it is so amazing when applying singleton design pattern for the exoplayer in this case.
public class YoutubeExecutor  {
    private final static int EXE_SONG_MESSAGE = 4124456;
    private final SingletonExoPlayer singletonExoPlayer;
    private ExecutorService executorService;
    public YoutubeExecutor(@NonNull Application application) {
//        super(application);
        singletonExoPlayer = SingletonExoPlayer.getInstance(application);
    }
    public void InitializeExecutor(){
        executorService = Executors.newFixedThreadPool(1);
    }
    public void MusicAsyncExecutor(String query, WeakReference<YoutubeMusicModel> youtubeMusicModelWeakReference, WeakReference<StyledPlayerControlView> controlViewWeakReference, WeakReference<TextView>NextSongTitle){
        ExoPlayer exoPlayer = singletonExoPlayer.getExoPlayer();
        controlViewWeakReference.get().setPlayer(exoPlayer);
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == EXE_SONG_MESSAGE){
                  //  MediaItem item = MediaItem.fromUri(youtubeMusicModelWeakReference.get().getMusicElement(msg.arg1).getDownloadedMusicUrl());
                    MediaItem item = new MediaItem.Builder().setUri(youtubeMusicModelWeakReference.get().getMusicElement(msg.arg1).getDownloadedMusicUrl())
                            .setMediaId(String.valueOf(msg.arg1))
                            .setTag(youtubeMusicModelWeakReference.get().getMusicElement(msg.arg1)).build();
                    if(youtubeMusicModelWeakReference.get()!=null)
                        if(!youtubeMusicModelWeakReference.get().isSuccesfulUpdateUI())
                        {
                            youtubeMusicModelWeakReference.get().setSuccesfulUpdateUI(true);
                            NextSongTitle.get().setText(youtubeMusicModelWeakReference.get().getMusicElement(msg.arg1).getTitle());
                        }

                    exoPlayer.addMediaItem(item);
                    if (!exoPlayer.isPlaying() || !exoPlayer.isCurrentMediaItemLive()){
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

                    Log.d("YouBug", "run: old number of element: "+ String.valueOf(youtubeMusicModelWeakReference.get().getLengthYoutubeList()));
                    // Todo: Be quick to do the optimization and update the i<2 (we set i<2 in order to save the resource)
                    for(int i=youtubeMusicModelWeakReference.get().getLastUpdateIndex(); i<2; i++){
                        title = searchedSongsArray.getJSONObject(i).getString("title");
                        Log.d("YouBug", "run: "+ title);
                        songId = searchedSongsArray.getJSONObject(i).getString("id");
                        Log.d("YouBug", "run: "+ songId);
                        youtubeMusicModelWeakReference.get().AddMusicElement(new YoutubeMusicElement(title,songId));
                        // To get the mp4 form from the ID of the youtube ID
                         client = new OkHttpClient();
                         request = new Request.Builder()
                                .url("https://easy-youtube.p.rapidapi.com/video/source/video?videoId="+songId)
                                .get()
                                .addHeader("x-rapidapi-host", "easy-youtube.p.rapidapi.com")
                                .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                                .build();

                         response = client.newCall(request).execute();
                         songUrl = new JSONObject(Objects.requireNonNull(response.body()).string()).getString("video");
                         youtubeMusicModelWeakReference.get().getMusicElement(i).setDownloadedMusicUrl(songUrl);
                        Log.d("YouBug", "run: bug in the message");
                         Message message = new Message();
                         message.what = EXE_SONG_MESSAGE;
                         message.arg1 = i;
                         handler.sendMessage(message);
                    }
                    youtubeMusicModelWeakReference.get().setLastUpdateIndex(youtubeMusicModelWeakReference.get().getLastUpdateIndex()+searchedSongsArray.length());
                    Log.d("YouBug", "run: new number of element: "+ String.valueOf(youtubeMusicModelWeakReference.get().getLengthYoutubeList()));
                } catch (IOException | JSONException  e) {
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();
        executorService = null;
    }
    public boolean isExecuting(){
        return executorService != null;
    }
}
