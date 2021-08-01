package com.maddog05.whatanime.core.network;

import androidx.core.util.Pair;

import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.whatanime.core.entity.SearchImageResult;
import com.maddog05.whatanime.core.entity.output.OutputGetQuota;

import java.io.File;
import java.util.List;

/**
 * Created by andreetorres on 23/09/17.
 */

public interface LogicNetwork {

    void searchWithPhoto(File file,
                            Callback<Pair<String, List<SearchImageResult>>> callback);

    void searchWithUrl(String url,
                          Callback<Pair<String, List<SearchImageResult>>> callback);

    void getQuota(Callback<Pair<String, OutputGetQuota>> callback);
}
