package com.abhi.galleryapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class photo {

    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("adult")
    @Expose
    private Boolean adult;
    @SerializedName("secret")
    @Expose
    private String secret;
    @SerializedName("farm")
    @Expose
    private String farm;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("ispublic")
    @Expose
    private String ispublic;
    @SerializedName("isfriend")
    @Expose
    private String isfriend;
    @SerializedName("isfamily")
    @Expose
    private String isfamily;
    @SerializedName("server")
    @Expose
    private String server;
    @SerializedName("URL")
    @Expose
    private String URL;

    public String getURL() {
        return "http://farm1.static.flickr.com/578/23451156376_8983a8ebc7.jpg";
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIspublic() {
        return ispublic;
    }

    public void setIspublic(String ispublic) {
        this.ispublic = ispublic;
    }

    public String getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(String isfriend) {
        this.isfriend = isfriend;
    }

    public String getIsfamily() {
        return isfamily;
    }

    public void setIsfamily(String isfamily) {
        this.isfamily = isfamily;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
