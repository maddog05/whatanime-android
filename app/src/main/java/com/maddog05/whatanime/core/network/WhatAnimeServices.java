package com.maddog05.whatanime.core.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by andreetorres on 23/09/17.
 */

public interface WhatAnimeServices {
    @POST("search")
    Call<JsonObject> search(@Body JsonObject encodedImage);//, @Field("filter") String filter

    @POST("search")
    Call<JsonObject> searchWithUrl(@Query("url") String url);

    @GET("me")
    Call<JsonObject> getQuota();
}
