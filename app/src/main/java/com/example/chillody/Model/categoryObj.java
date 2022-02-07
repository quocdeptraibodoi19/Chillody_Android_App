package com.example.chillody.Model;

public class categoryObj {
    private String name;
    private String url;
    public categoryObj(String name, String url){
        this.name = name;
        this.url = url;
    }
    public String getUrl(){
        return url;
    }
    public String getName(){
        return name;
    }
}
