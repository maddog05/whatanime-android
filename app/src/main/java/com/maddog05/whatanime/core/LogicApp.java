package com.maddog05.whatanime.core;

import android.content.Context;
import android.graphics.Bitmap;

import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.whatanime.core.entity.RequestEntity;
import com.maddog05.whatanime.core.entity.ResponseEntity;
import com.maddog05.whatanime.core.entity.SearchAnimeResponse;

import java.util.List;

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

    void databaseStart(Context context);

    long databaseCreateRequest(RequestEntity request);

    void databaseUpdateRequest(RequestEntity request);

    void databaseSetResponses(long requestId, List<ResponseEntity> response);

    RequestEntity databaseGetRequest(long requestId);

    List<RequestEntity> databaseGetAllRequests();

    void databaseClearRequests();
}
