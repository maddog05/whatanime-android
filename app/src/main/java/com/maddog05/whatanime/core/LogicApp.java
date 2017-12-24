package com.maddog05.whatanime.core;

import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.whatanime.core.entity.SearchAnimeResponse;

/**
 * Created by andreetorres on 23/09/17.
 */

public interface LogicApp {
    boolean isFirstTutorial();

    void finishFirstTutorial();

    void searchAnime(String encoded, String filter, Callback<SearchAnimeResponse> callback);
}
