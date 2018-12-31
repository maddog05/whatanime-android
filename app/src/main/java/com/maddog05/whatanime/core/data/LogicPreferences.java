package com.maddog05.whatanime.core.data;

/**
 * Created by andree on 26/09/2017.
 */

public interface LogicPreferences {
    int getLastChangelogVersion();

    void setLastChangelogVersion(int version);

    boolean getHContentEnabled();

    void setHContentEnabled(boolean isEnabled);
}
