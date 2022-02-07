package com.example.chillody.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class UnsplashImgElement {
    private String regularURL,smallURL;
    private String ArtistName, InstagramAccount;
    private String Description;
    public UnsplashImgElement(JSONObject object) throws JSONException {
        ArtistName = object.getJSONObject("user").getString("name");
        InstagramAccount = object.getJSONObject("user").getString("instagram_username");
        regularURL = object.getJSONObject("urls").getString("regular");
        smallURL = object.getJSONObject("urls").getString("small");
        Description = object.getString("description");
    }
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
}
