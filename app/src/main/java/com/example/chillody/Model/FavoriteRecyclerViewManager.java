package com.example.chillody.Model;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class FavoriteRecyclerViewManager {
    private final RecyclerView chillingRecyclerView, cafeRecyclerView, ghibliRecyclerView;
    private int position ;
    private int flag ; // 1 - chilling, 2 - cafe, 3 - ghibli
    private int chillingSize, cafeSize, ghibliSize;
    public FavoriteRecyclerViewManager(RecyclerView chillingRecyclerView, RecyclerView cafeRecyclerView, RecyclerView ghibliRecyclerView){
        this.chillingRecyclerView = chillingRecyclerView;
        this.cafeRecyclerView = cafeRecyclerView;
        this.ghibliRecyclerView = ghibliRecyclerView;
        this.position = -1;
        this.flag = -1;
        this.chillingSize = 0;
        this.cafeSize = 0;
        this.ghibliSize = 0;
    }

    public int getChillingSize() {
        return chillingSize;
    }

    public int getCafeSize() {
        return cafeSize;
    }

    public int getGhibliSize() {
        return ghibliSize;
    }

    public void setChillingSize(int chillingSize) {
        this.chillingSize = chillingSize;
    }

    public void setCafeSize(int cafeSize) {
        this.cafeSize = cafeSize;
    }

    public void setGhibliSize(int ghibliSize) {
        this.ghibliSize = ghibliSize;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition(){
        return this.position;
    }
    public String getType(){
        switch (flag){
            case 1:
               return "ChillingLove";
            case 2:
                return "CafeLove";
            case 3:
                return "GhibliLove";
            default:
                return null;
        }
    }
    public void setFlag(String flag) {
        switch (flag){
            case "ChillingLove":
                this.flag = 1;
                break;
            case "CafeLove":
                this.flag = 2;
                break;
            case "GhibliLove":
                this.flag = 3;
                break;
            default:
                this.flag = -1;
                break;
        }
    }

    public View getCurrentChildView(){
        if(flag == -1 || position == -1) {
            Log.d("BUGLOILOL", "getCurrentChildView: flag == -1 or position == -1");
            if(flag == -1)
                Log.d("BUGLOILOL", "getCurrentChildView: flag == -1");
            else    Log.d("BUGLOILOL", "getCurrentChildView: position == -1");
            return null;
        }
        switch (flag){
            case 1:
                if(position >= chillingSize){
                    Log.d("BUGLOILOL", "getCurrentChildView: the chillingsize > position");
                    return null;
                }
               return Objects.requireNonNull(chillingRecyclerView.getLayoutManager()).findViewByPosition(position);
            case 2:
                if(position >= cafeSize) {
                    Log.d("BUGLOILOL", "getCurrentChildView: the cafesize > position");
                    return null;
                }
                return Objects.requireNonNull(cafeRecyclerView.getLayoutManager()).findViewByPosition(position);
            case 3:
                if(position >= ghibliSize) {
                    Log.d("BUGLOILOL", "getCurrentChildView: the ghiblisize > position");
                    return null;
                }
                return Objects.requireNonNull(ghibliRecyclerView.getLayoutManager()).findViewByPosition(position);
            default:
                Log.d("BUGLOILOL", "getCurrentChildView: the type is different");
                return null;
        }
    }
}
