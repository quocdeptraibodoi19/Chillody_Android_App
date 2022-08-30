package com.example.chillody.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.TypeConverter;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {FavoriteYoutubeElement.class,YoutubeMusicElement.class,UnsplashImgElement.class},version = 4,exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    public abstract FavoriteYoutubeDao favoriteYoutubeDao();
    // Normally, when you want to add new Dao object into the project, you have to repair the class a little bit.
    // First, you have to provide the full setter and getter methods of all columns in the entity and provide the default constructor or a constructor only
   // If you provide it with multiple constructors, it will be not able to pick a valid one. In this case, normally we will set ignore for the unwanted
    // constructor. if you use it with a particular constructor, it has to match with the parameter. Otherwise, you can set it with the
    // default constructor and you can cancel out the whole set of ignore keywords on each constructor.
    public abstract YoutubeMusicDao youtubeMusicDao();
    public abstract UnsplashImageDao unsplashImageDao();
    public static RoomDatabase instance;
    public static RoomDatabase getInstance(Application application){
        if(instance == null){
            synchronized (RoomDatabase.class){
                if(instance == null){
                    instance = Room.databaseBuilder(application,RoomDatabase.class,"ChillodyDatababse")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
