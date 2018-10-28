package com.abhi.galleryapp.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class photos {

    @SerializedName("page")
    @Expose
    private String page;
    @SerializedName("photo")
    @Expose
    private List<photo> photo = new ArrayList<photo>();
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("pages")
    @Expose
    private String pages;
    @SerializedName("perpage")
    @Expose
    private String perpage;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public List<com.abhi.galleryapp.models.photo> getPhoto() {
        return photo;
    }

    public void setPhoto(List<com.abhi.galleryapp.models.photo> photo) {
        this.photo = photo;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPerpage() {
        return perpage;
    }

    public void setPerpage(String perpage) {
        this.perpage = perpage;
    }
}
