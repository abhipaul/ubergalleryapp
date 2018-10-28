package com.abhi.galleryapp.api;

import com.abhi.galleryapp.models.PhotoDO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PhotoService {

    @GET("services/rest/")
    Call<PhotoDO> getAllPhotosFromService(
            @Query("method") String method,
            @Query("api_key") String api_key,
            @Query("format") String format,
            @Query("nojsoncallback") String nojsoncallback,
            @Query("text") String text,
            @Query("safe_search") String safe_search,
            @Query("page") int page
    );
}
