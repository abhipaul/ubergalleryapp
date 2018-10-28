package com.abhi.galleryapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoDO
{
    @SerializedName("photos")
    @Expose
    private photos photos;
    @SerializedName("stat")
    @Expose
    private String stat;

    public photos getPhotos ()
    {
        return photos;
    }

    public void setPhotos (photos photos)
    {
        this.photos = photos;
    }

    public String getStat ()
    {
        return stat;
    }

    public void setStat (String stat)
    {
        this.stat = stat;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [photos = "+photos+", stat = "+stat+"]";
    }
}

