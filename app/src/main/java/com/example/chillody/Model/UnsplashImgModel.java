package com.example.chillody.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnsplashImgModel extends AndroidViewModel {
   private List<UnsplashImgElement> unsplashImgElementList;
   private int cur;
   private int CurPage;
   public UnsplashImgModel(@NonNull Application application){
       super(application);
       unsplashImgElementList = new ArrayList<>();
       cur =0;
       CurPage = 1;
   }
   public void addElement(UnsplashImgElement element){
       unsplashImgElementList.add(element);
   }
   public UnsplashImgElement getCurrentElement(){
       return unsplashImgElementList.get(cur);
   }
   public UnsplashImgElement getElement(int i){
       return unsplashImgElementList.get(i);
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

}
