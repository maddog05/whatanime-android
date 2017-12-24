package com.maddog05.whatanime;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatDelegate;

import com.maddog05.maddogutilities.logger.Logger2;

/**
 * Created by andreetorres on 24/09/17.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Logger2.get().setEnabled(BuildConfig.IS_LOGGER_ENABLED);
    }
}
