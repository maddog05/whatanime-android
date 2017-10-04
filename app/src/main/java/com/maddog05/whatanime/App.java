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

    public static String getAppVersion(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException nnfE) {
            packageInfo = null;
        }
        if (packageInfo == null)
            return BuildConfig.VERSION_NAME;
        else
            return packageInfo.versionName;
    }
}
