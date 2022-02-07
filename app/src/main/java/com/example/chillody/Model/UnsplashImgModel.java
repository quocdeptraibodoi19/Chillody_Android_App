package com.example.chillody.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnsplashImgModel {
   private List<UnsplashImgElement> unsplashImgElementList;
   private int cur;
   private int CurPage;
   public UnsplashImgModel(){
       unsplashImgElementList = new ArrayList<>();
       cur = 0;
       CurPage = 1;
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
       if(cur!=0) cur --;
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
