package com.maddog05.whatanime.core;

import android.graphics.Bitmap;

import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.whatanime.core.entity.SearchAnimeResponse;

/*
 * Created by andreetorres on 23/09/17.
 */

public interface LogicApp {
    boolean isFirstTutorial();

    void finishFirstTutorial();

    boolean isChangelogViewed();

    void finishChangelogViewed();

    void searchAnime(String encoded,
                     String filter,
                     Callback<SearchAnimeResponse> callback);

    void loadImageUrl(String url,
                      Callback<Bitmap> callback);
}
