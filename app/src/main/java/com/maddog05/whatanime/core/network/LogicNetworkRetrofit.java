package com.maddog05.whatanime.core.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.util.Pair;

import com.google.gson.JsonObject;
import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.maddogutilities.logger.Logger2;
import com.maddog05.whatanime.BuildConfig;
import com.maddog05.whatanime.R;
import com.maddog05.whatanime.core.entity.SearchDetail;
import com.maddog05.whatanime.util.C;
import com.maddog05.whatanime.util.Mapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by andreetorres on 23/09/17.
 */

public class LogicNetworkRetrofit implements LogicNetwork {

    private static final String TAG = "#Retrofit";

    private static final String URL = "https://whatanime.ga/api/";
    private static final String TOKEN = BuildConfig.TOKEN;
    private static final long TIMEOUT = 60;

    private WhatAnimeServices services;

    public static LogicNetworkRetrofit newInstance() {
        return new LogicNetworkRetrofit();
    }

    private LogicNetworkRetrofit() {
        init();
    }

    private void init() {
        OkHttpClient client = eatThatHttps();
        if (client != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(URL)
                    .client(client)
                    .build();
            services = retrofit.create(WhatAnimeServices.class);
        }
    }

    private void logStart(String text) {
        logSeparator();
        Logger2.get().d(TAG, text);
    }

    private void logSeparator() {
        Logger2.get().d(TAG, "------");
    }

    @Override
    public void searchWithPhoto(final Context context,
                                String encodedImage,
                                final Callback<Pair<String, SearchDetail>> callback) {
        logStart("searchWithPhoto");
        if (services != null) {
            services.search(TOKEN, encodedImage).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Logger2.get().d(TAG, "searchWithPhoto: onResponse: httpCode = " + response.code());
                    if (response.body() != null) {
                        Logger2.get().d(TAG, "searchWithPhoto: onResponse: body = " + response.body());
                        SearchDetail searchDetail = Mapper.parseSearchDetail(response.body());
                        if (searchDetail.quota > 0) {
                            callback.done(Pair.create(C.EMPTY, searchDetail));
                            logSeparator();
                        } else {
                            String errorMessage = getString(context, R.string.error_service_quota_all_used)
                                    + C.SPACE
                                    + searchDetail.expire;
                            callback.done(Pair.create(errorMessage, new SearchDetail()));
                            logSeparator();
                        }
                    } else {
                        String errorMessage = String.valueOf(response.code() + C.SPACE + getString(context, R.string.error_service_response_or_body_null));
                        Logger2.get().e(TAG, "searchWithPhoto: onResponse: " + errorMessage);
                        callback.done(Pair.create(errorMessage, new SearchDetail()));
                        logSeparator();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        Logger2.get().e(TAG, "searchWithPhoto: onFailure: " + t.toString());
                        callback.done(Pair.create(t.getMessage(), new SearchDetail()));
                        logSeparator();
                    } else {
                        Logger2.get().e(TAG, "searchWithPhoto: onFailure: unknown error");
                        callback.done(Pair.create(getString(context,
                                R.string.error_service_unknown), new SearchDetail()));
                        logSeparator();
                    }
                }
            });
        } else {
            Logger2.get().e(TAG, "searchWithPhoto: services is null");
            logSeparator();
            callback.done(Pair.create(getString(context, R.string.error_service_not_initialized), new SearchDetail()));
        }
    }

    //ACCESS TO CONTEXT
    private String getString(Context context, int resString) {
        return context.getString(resString);
    }

    //HTTPS
    private static OkHttpClient eatThatHttps() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.1");
            ctx.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            }, null);
            SSLSocketFactory socketFactory = new MaddogTLSSocketFactory();
            return new OkHttpClient.Builder()
                    .sslSocketFactory(socketFactory, new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    })
                    .hostnameVerifier(new HostnameVerifier() {
                        @SuppressLint("BadHostnameVerifier")
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static class MaddogTLSSocketFactory extends SSLSocketFactory {
        private SSLSocketFactory internalSSLSocketFactory;

        public MaddogTLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            internalSSLSocketFactory = context.getSocketFactory();
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return internalSSLSocketFactory.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return internalSSLSocketFactory.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket(s, host, port, autoClose));
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port, localHost, localPort));
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return enableTLSOnSocket(internalSSLSocketFactory.createSocket(address, port, localAddress, localPort));
        }

        private Socket enableTLSOnSocket(Socket socket) {
            if (socket != null && (socket instanceof SSLSocket)) {
                ((SSLSocket) socket).setEnabledProtocols(new String[]{"TLSv1.1", "TLSv1.2"});
            }
            return socket;
        }
    }
}
