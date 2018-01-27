package com.maddog05.whatanime.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;

import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.whatanime.BuildConfig;
import com.maddog05.whatanime.core.data.LogicPreferences;
import com.maddog05.whatanime.core.entity.SearchAnimeResponse;
import com.maddog05.whatanime.core.entity.SearchDetail;
import com.maddog05.whatanime.core.image.GlideLoader;
import com.maddog05.whatanime.core.network.LogicNetwork;
import com.maddog05.whatanime.util.C;

/*
 * Created by andreetorres on 23/09/17.
 */

public class LogicAppImpl implements LogicApp {
    private Context context;
    private LogicNetwork network;
    private LogicPreferences preferences;

    static LogicAppImpl newInstace(Context context,
                                   LogicNetwork network,
                                   LogicPreferences preferences) {
        LogicAppImpl instance = new LogicAppImpl();
        instance.context = context;
        instance.network = network;
        instance.preferences = preferences;
        return instance;
    }

    @Override
    public boolean isFirstTutorial() {
        return preferences.isFirstTutorial();
    }

    @Override
    public void finishFirstTutorial() {
        preferences.finishFirstTutorial();
    }

    @Override
    public boolean isChangelogViewed() {
        int currentVersion = BuildConfig.VERSION_CODE;
        return preferences.getLastChangelogVersion() == currentVersion;
    }

    @Override
    public void finishChangelogViewed() {
        int currentVersion = BuildConfig.VERSION_CODE;
        preferences.setLastChangelogVersion(currentVersion);
    }

    @Override
    public void searchAnime(String encoded,
                            String filter,
                            final Callback<SearchAnimeResponse> callback) {
        network.searchWithPhoto(context,
                encoded,
                filter,
                new Callback<Pair<String, SearchDetail>>() {
                    @Override
                    public void done(Pair<String, SearchDetail> pair) {
                        SearchAnimeResponse response = new SearchAnimeResponse();
                        if (pair.first.isEmpty()) {
                            response.errorMessage = C.EMPTY;
                            response.searchDetail = pair.second;
                        } else {
                            response.errorMessage = pair.first;
                            response.searchDetail = null;
                        }
                        callback.done(response);
                    }
                });
    }

    @Override
    public void loadImageUrl(String url,
                             final Callback<Bitmap> callback) {
        GlideLoader imageLoader = GlideLoader.create();
        imageLoader.with(context)
                .load(url);
        imageLoader.loadAsBitmap(new Callback<Bitmap>() {
            @Override
            public void done(Bitmap bitmap) {
                callback.done(bitmap);
            }
        });
    }
}
