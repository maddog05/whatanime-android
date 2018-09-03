package com.maddog05.whatanime.ui.tor;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.maddog05.maddogutilities.android.AndroidVersions;
import com.maddog05.whatanime.R;
import com.maddog05.whatanime.core.entity.SearchDetail;
import com.maddog05.whatanime.ui.activity.HomeActivity;
import com.maddog05.whatanime.ui.activity.InformationActivity;
import com.maddog05.whatanime.ui.activity.VideoPreviewActivity;
import com.maddog05.whatanime.ui.activity.VideoSelectActivity;
import com.maddog05.whatanime.ui.activity.SettingsActivity;
import com.maddog05.whatanime.util.C;

/*
 * Created by andreetorres on 27/09/17.
 */

public class Navigator {
    @SuppressLint("NewApi")
    public static void goToMain(AppCompatActivity activity, View view) {
        Intent intent = new Intent(activity, HomeActivity.class);
        if (AndroidVersions.isMarshmallow()) {
            ActivityOptions activityOptions = ActivityOptions.makeClipRevealAnimation(view,
                    0,
                    0,
                    view.getMeasuredWidth(),
                    view.getMeasuredHeight());
            activity.startActivity(intent, activityOptions.toBundle());
            activity.finishAffinity();

        } else {
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeClipRevealAnimation(view,
                    0,
                    0,
                    view.getMeasuredWidth(),
                    view.getMeasuredHeight());

            ActivityCompat.startActivity(activity, intent, activityOptionsCompat.toBundle());
            ActivityCompat.finishAffinity(activity);
        }
    }

    public static void goToPreviewVideo(AppCompatActivity activity, String url, SearchDetail.Doc doc) {
        ActivityOptionsCompat aoc;
        aoc = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(activity, VideoPreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(C.Extras.VIDEO_URL, url);
        bundle.putParcelable(C.Extras.DOC, doc);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(activity, intent, aoc.toBundle());
    }

    public static void goToSelectVideo(AppCompatActivity activity, String path, int requestCode) {
        ActivityOptionsCompat aoc;
        aoc = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(activity, VideoSelectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(C.Extras.SELECT_LOCAL_VIDEO_PATH, path);
        intent.putExtras(bundle);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, aoc.toBundle());
    }

    public static void shareText(Context context, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.action_share)));
    }

    public static void goToInformation(Context context) {
        context.startActivity(new Intent(context, InformationActivity.class));
    }

    public static void goToWebBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.indicator_choose_browser)));
    }

    public static Intent getIntentSelectImage(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        return Intent.createChooser(intent, context.getString(R.string.indicator_choose_gallery_app));
    }

    public static Intent getIntentSelectVideo(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        return Intent.createChooser(intent, context.getString(R.string.indicator_choose_gallery_app));
    }

    public static Intent getIntentSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }
}
