package com.maddog05.whatanime.core.network;

import android.content.Context;
import androidx.core.util.Pair;

import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.whatanime.core.entity.output.OutputGetQuota;
import com.maddog05.whatanime.core.entity.output.SearchDetail;

/**
 * Created by andreetorres on 23/09/17.
 */

public interface LogicNetwork {
    void searchWithPhoto(Context context,
                         String encodedImage,
                         String filter,
                         Callback<Pair<String, SearchDetail>> callback);

    void searchWithUrl(Context context,
                       String url,
                       Callback<Pair<String, SearchDetail>> callback);

    void getQuota(Context context,
                  Callback<Pair<String, OutputGetQuota>> callback);
}
