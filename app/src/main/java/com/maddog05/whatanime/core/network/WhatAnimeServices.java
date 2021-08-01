package com.maddog05.whatanime.core.network;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by andreetorres on 23/09/17.
 */

public interface WhatAnimeServices {
    @Multipart
    @POST("search")
    Call<JsonObject> searchWithFile(@Part MultipartBody.Part filePart);

    @POST("search")
    Call<JsonObject> searchWithUrl(@Query("url") String url);

    @GET("me")
    Call<JsonObject> getQuota();
}
