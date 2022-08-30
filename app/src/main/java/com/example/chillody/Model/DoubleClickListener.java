package com.example.chillody.Model;

import android.os.Handler;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class DoubleClickListener implements View.OnClickListener {
    // This is the class for the Double Click Feature.
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

    long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        // We record the time that user click
        // if the time interval between 2 last clicks is less than 300 ms, then it will invoke the abstract method  onDoubleClick
        // the variable to record the time is count
        // if the time interval is bigger or equal to 300 ms, we will set count = 0
        // to do that without effecting the UI thread, we sleep the worker thread as done bellow
        // there are 3 ways to do that: via runnable object, thread object and ExecutorService object
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
            onDoubleClick(v);
        }
        lastClickTime = clickTime;
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                lastClickTime = 0;
            }
        };
        handler.postDelayed(runnable,300);
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                try {
//                    sleep(301);
//                    lastClickTime = 0;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        thread.start();
    }

    public abstract void onDoubleClick(View v);
}