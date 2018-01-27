package com.maddog05.whatanime.core.data;

/**
 * Created by andree on 26/09/2017.
 */

public interface LogicPreferences {
    boolean isFirstTutorial();

    void finishFirstTutorial();

    int getLastChangelogVersion();

    void setLastChangelogVersion(int version);
}
