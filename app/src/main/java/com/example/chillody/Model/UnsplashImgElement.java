package com.example.chillody.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "unsplash_image_table")
public class UnsplashImgElement {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "RegularUrl")
    private String regularURL;
    @ColumnInfo (name = "SmallUrl")
    private String smallURL;
    @ColumnInfo
    private String ArtistName;
    @ColumnInfo
    private String InstagramAccount;
    @ColumnInfo
    private String Description;
    @ColumnInfo( name = "Type")
    private String type;
    @ColumnInfo
    private int isFavorite = 0;
    public UnsplashImgElement(JSONObject object,String type) throws JSONException {
        ArtistName = object.getJSONObject("user").getString("name");
        InstagramAccount = object.getJSONObject("user").getString("instagram_username");
        regularURL = object.getJSONObject("urls").getString("regular");
        smallURL = object.getJSONObject("urls").getString("small");
        Description = object.getString("description");
        this.type = type;
    }
    public UnsplashImgElement(@NonNull String regularURL, String smallURL, String artistName, String instagramAccount, String description,String type){
        this.regularURL = regularURL;
        this.smallURL = smallURL;
        this.ArtistName = artistName;
        this.InstagramAccount = instagramAccount;
        this.Description = description;
        this.type = type;
    }
    // if we want the system to sensor this object as the DAO object, you have to define exhaustively getter and setter methods,
    // and the default constructor or (if you do not want the default constructor, you can use Ignore keyword)
    public UnsplashImgElement(){
        regularURL = "";
        smallURL = "";
        ArtistName = "";
        InstagramAccount = "";
        Description = "";
        type = "";
    }
    @NonNull
    public String getRegularURL() {
        return regularURL;
    }

    public String getSmallURL() {
        return smallURL;
    }

    public String getArtistName() {
        return ArtistName;
    }

    public String getInstagramAccount() {
        return InstagramAccount;
    }

    public String getDescription() {
        return Description;
    }

    public String getType(){
        return type;
    }

    public int getIsFavorite(){
        return this.isFavorite;
    }

    public void setRegularURL(@NonNull String regularURL){
        this.regularURL = regularURL;
    }

    public void setSmallURL(String smallURL){
        this.smallURL = smallURL;
    }

    public void setArtistName(String artistName){
        this.ArtistName = artistName;
    }

    public void setInstagramAccount(String instagramAccount){
        this.InstagramAccount = instagramAccount;
    }

    public void setDescription(String description){
        this.Description = description;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setIsFavorite(int isFavorite){
        this.isFavorite = isFavorite;
    }
}
