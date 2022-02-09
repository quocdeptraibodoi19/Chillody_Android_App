package com.example.chillody.Networking;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeneralExecutorService extends AndroidViewModel {
    private final ExecutorService executorService;
    private static GeneralExecutorService generalExecutorService;
    private GeneralExecutorService(@NonNull Application application) {
        super(application);
        executorService = Executors.newFixedThreadPool(3);
    }
    public static GeneralExecutorService getInstance(@NonNull Application application){
        if(generalExecutorService == null)
            generalExecutorService = new GeneralExecutorService(application);
        return generalExecutorService;
    }
    public ExecutorService getExecutorService(){
        return executorService;
    }
}
