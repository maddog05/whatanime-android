package com.maddog05.whatanime.core.network

import android.annotation.SuppressLint
import android.webkit.MimeTypeMap
import androidx.core.util.Pair
import com.google.gson.JsonObject
import com.maddog05.maddogutilities.callback.Callback
import com.maddog05.whatanime.BuildConfig
import com.maddog05.whatanime.core.entity.SearchImageResult
import com.maddog05.whatanime.core.entity.output.OutputGetQuota
import com.maddog05.whatanime.util.Mapper
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class LogicNetworkKotlin :LogicNetwork{

    private var api: WhatAnimeServices

    init {
        api = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.SERVER_DOMAIN)
            .client(createHttpsClient())
            .build()
            .create(WhatAnimeServices::class.java)
    }

    override fun searchWithPhoto(
        file: File,
        callback: Callback<Pair<String, List<SearchImageResult>>>
    ) {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension) ?: ""
        val requestBody = RequestBody.create(MediaType.parse(mimeType), file)
        val filePart = MultipartBody.Part.createFormData(
            "image",
            file.name,
            requestBody
        )
        api.searchWithFile(filePart).enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val responseBody = response.body() ?: JsonObject()
                val errorMessage = Mapper.elementString(responseBody.get("error"), "")
                if (errorMessage.isEmpty() && response.isSuccessful) {
                    val jsonResults = Mapper.elementArray(responseBody.get("result"))
                    val searchResults = mutableListOf<SearchImageResult>()
                    for (i in 0 until jsonResults.size())
                        searchResults.add(SearchImageResult.parseJson(jsonResults.get(i).asJsonObject))
                    callback.done(Pair.create("", searchResults))
                } else
                    callback.done(Pair.create(errorMessage, listOf()))
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                callback.done(Pair.create(t.toString(), listOf()))
            }
        })
    }

    override fun searchWithUrl(
        url: String,
        callback: Callback<Pair<String, List<SearchImageResult>>>
    ) {
        api.searchWithUrl(url).enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val responseBody = response.body() ?: JsonObject()
                val errorMessage = Mapper.elementString(responseBody.get("error"), "")
                if (errorMessage.isEmpty() && response.isSuccessful) {
                    val jsonResults = Mapper.elementArray(responseBody.get("result"))
                    val searchResults = mutableListOf<SearchImageResult>()
                    for (i in 0 until jsonResults.size())
                        searchResults.add(SearchImageResult.parseJson(jsonResults.get(i).asJsonObject))
                    callback.done(Pair.create("", searchResults))
                } else
                    callback.done(Pair.create(errorMessage, listOf()))
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                callback.done(Pair.create(t.toString(), listOf()))
            }

        })
    }

    override fun getQuota(callback: Callback<Pair<String, OutputGetQuota>>) {
        api.quota.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val responseBody = response.body() ?: JsonObject()
                val errorMessage = Mapper.elementString(responseBody.get("error"), "")
                if (errorMessage.isEmpty() && response.isSuccessful)
                    callback.done(Pair.create(errorMessage, Mapper.parseGetQuota(responseBody)))
                else
                    callback.done(Pair.create("ERROR ${response.code()}", OutputGetQuota()))
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                callback.done(Pair.create(t.toString(), OutputGetQuota()))
            }
        })
    }

    //RETROFIT
    private fun createHttpsClient(): OkHttpClient {
        var builder: OkHttpClient.Builder
        try {
            val x509TrustManager = object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            val trustAllCerts = arrayOf(x509TrustManager)

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            builder = OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0])
                .hostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            e.printStackTrace()
            builder = OkHttpClient.Builder()
        }
        builder.connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        return builder.build()
    }
}