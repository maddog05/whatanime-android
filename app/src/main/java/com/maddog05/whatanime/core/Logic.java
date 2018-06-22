package com.maddog05.whatanime.core;

import android.content.Context;

import com.maddog05.maddogutilities.image.ImageLoader;
import com.maddog05.whatanime.core.data.LogicPreferenceSharedPref;
import com.maddog05.whatanime.core.data.LogicPreferences;
import com.maddog05.whatanime.core.database.LogicDatabase;
import com.maddog05.whatanime.core.database.LogicDatabaseRealm;
import com.maddog05.whatanime.core.image.GlideLoader;
import com.maddog05.whatanime.core.network.LogicNetwork;
import com.maddog05.whatanime.core.network.LogicNetworkRetrofit;

/**
 * Created by andreetorres on 23/09/17.
 */

public class Logic {
    public static LogicApp get(Context context) {
        return LogicAppImpl.newInstace(context,
                getNetwork(),
                getPreferences(context),
                database());
    }

    public static LogicDatabase database() {
        return new LogicDatabaseRealm();
    }

    public static ImageLoader imageLoader(Context context) {
        return GlideLoader.create();
    }

    private static LogicPreferences getPreferences(Context context) {
        return LogicPreferenceSharedPref.newInstance(context);
    }

    private static LogicNetwork getNetwork() {
        return LogicNetworkRetrofit.newInstance();
    }

}
