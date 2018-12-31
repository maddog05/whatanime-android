package com.maddog05.whatanime.core.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by andreetorres on 23/09/17.
 */

public interface WhatAnimeServices {
    @FormUrlEncoded
    @POST("search")
    Call<JsonObject> search(@Query("token") String token, @Field("image") String encodedImage);//, @Field("filter") String filter

    @GET("me")
    Call<JsonObject> getQuota(@Query("token") String token);
}
