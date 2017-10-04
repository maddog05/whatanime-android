package com.maddog05.whatanime.core.network;

import android.content.Context;
import android.support.v4.util.Pair;

import com.google.gson.JsonObject;
import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.whatanime.core.entity.SearchDetail;

/**
 * Created by andreetorres on 23/09/17.
 */

public interface LogicNetwork {
    void searchWithPhoto(Context context,
                         String encodedImage,
                         Callback<Pair<String, SearchDetail>> callback);
}
