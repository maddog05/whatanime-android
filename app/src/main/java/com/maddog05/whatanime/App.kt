package com.maddog05.whatanime

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.maddog05.maddogutilities.logger.Logger2

/*
 * Created by andreetorres on 23/12/17.
 */
class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        Logger2.get().setEnabled(BuildConfig.IS_LOGGER_ENABLED)
    }
}