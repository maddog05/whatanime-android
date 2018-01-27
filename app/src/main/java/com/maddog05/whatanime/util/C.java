package com.maddog05.whatanime.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.maddog05.whatanime.BuildConfig;

import java.util.Calendar;

/**
 * Created by andreetorres on 23/09/17.
 */

public class C {
    public static final String EMPTY = "";
    public static final int INTEGER_NONE = -1;
    public static final String SPACE = " ";
    public static final String FILTER_DEFAULT = "*";

    public static class Extras {
        public static final String VIDEO_URL = "com.maddog05.whatanime_extraVideoUrl";
        public static final String DOC = "com.maddog05.whatanime_extraDoc";
        public static final String SELECT_LOCAL_VIDEO_PATH = "com.maddog05.whatanime_extraSelectLocalVideoPath";
        public static final String VIDEO_FRAME_BITMAP = "com.maddog05.whatanime_extraVideoFrameBitmap";
    }

    public static int currentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
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
