package com.maddog05.whatanime

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.maddog05.maddogutilities.logger.Logger2
import com.maddog05.whatanime.core.Logic

/*
 * Created by andreetorres on 23/12/17.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Logic.get(this).databaseStart(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        Logger2.get().setEnabled(BuildConfig.IS_LOGGER_ENABLED)
    }
}