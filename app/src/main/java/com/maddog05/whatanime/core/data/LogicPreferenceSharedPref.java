package com.maddog05.whatanime.core.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by andree on 26/09/2017.
 */

public class LogicPreferenceSharedPref implements LogicPreferences {

    private static final String PREFERENCE_MAIN = "com.maddog05.whatanime_preferenceMain";

    private static final String LAST_CHANGELOG_VERSION = "lastChangelogVersion";
    private static final String H_CONTENT_ENABLED = "hContentEnabled";

    private Context context;

    private LogicPreferenceSharedPref(Context context) {
        this.context = context;
    }

    public static LogicPreferenceSharedPref newInstance(Context context) {
        return new LogicPreferenceSharedPref(context);
    }

    private SharedPreferences get() {
        return context.getSharedPreferences(PREFERENCE_MAIN, Context.MODE_PRIVATE);
    }

    @Override
    public int getLastChangelogVersion() {
        SharedPreferences preferences = get();
        return preferences.getInt(LAST_CHANGELOG_VERSION, 0);
    }

    @Override
    public void setLastChangelogVersion(int version) {
        SharedPreferences preferences = get();
        preferences.edit().putInt(LAST_CHANGELOG_VERSION, version).commit();
    }

    @Override
    public boolean getHContentEnabled() {
        SharedPreferences preferences = get();
        return preferences.getBoolean(H_CONTENT_ENABLED, false);
    }

    @Override
    public void setHContentEnabled(boolean isEnabled) {
        SharedPreferences preferences = get();
        preferences.edit().putBoolean(H_CONTENT_ENABLED, isEnabled).commit();
    }
}
