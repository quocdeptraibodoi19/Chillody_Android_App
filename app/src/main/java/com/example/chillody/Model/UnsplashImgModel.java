package com.example.chillody.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnsplashImgModel extends AndroidViewModel {
   private final List<UnsplashImgElement> unsplashImgElementList;
   private int cur;
   private int CurPage;
   private final UnsplashImageRepository repository;
   public UnsplashImgModel(@NonNull Application application){
       super(application);
       repository = new UnsplashImageRepository(application);
       unsplashImgElementList = new ArrayList<>();
       cur =0;
       CurPage = 1;
   }
   public LiveData<List<UnsplashImgElement>> getObservableUnsplashImgList(String type){
       return repository.getUnsplashImgList(type);
   }
   public void addElementIntoRes(UnsplashImgElement element){
       repository.insertUnsplashImgElement(element);
   }
   public void addElement(UnsplashImgElement element){
       unsplashImgElementList.add(element);
   }
   public UnsplashImgElement getCurrentElement(){
       return unsplashImgElementList.get(cur);
   }
   public void NextOne(){
       cur++;
   }
   public void PreviousOne(){
       if(cur!=0)  cur--;
   }
   public void UpdatePage(){
       CurPage++;
   }
   public int getCurPage(){
       return CurPage;
   }
   // here is to get the remained number of images in the list currently
   public int getRemainedNumber(){
       return  unsplashImgElementList.size() - cur;
   }
   public int getCur(){
       return cur;
   }
   public void setCurPage(int curPage){
       this.CurPage = curPage;
   }
   public void setCurPosition(int curPosition){
       this.cur = curPosition;
   }
   public boolean isContainSomething(){
       return unsplashImgElementList.size() != 0 ;
   }
}
