package com.abhi.galleryapp.common;

import android.app.Application;
import android.os.Environment;

import com.abhi.galleryapp.httpimage.FileSystemPersistence;
import com.abhi.galleryapp.httpimage.HttpImageManager;

public class MyApplication extends Application
{
    private static MyApplication myApplication ;
    private HttpImageManager mHttpImageManager;
    private  String CACHE_DIR_PATH					= Environment.getExternalStorageDirectory() + "/.GalleryFiles/Cache";

    @Override
    public void onCreate()
    {
        super.onCreate();

        myApplication = this;
        mHttpImageManager = new HttpImageManager(HttpImageManager.createDefaultMemoryCache(), new FileSystemPersistence(CACHE_DIR_PATH));
    }


    public HttpImageManager getHttpImageManager() {
        return mHttpImageManager;
    }

    public static MyApplication getMyApplication()
    {
        return myApplication;
    }
}
