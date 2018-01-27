package com.maddog05.whatanime.ui.tor;

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
import com.maddog05.whatanime.ui.InformationActivity;
import com.maddog05.whatanime.ui.MainActivity;
import com.maddog05.whatanime.ui.VideoPreviewActivity;
import com.maddog05.whatanime.ui.VideoSelectActivity;
import com.maddog05.whatanime.util.C;

/*
 * Created by andreetorres on 27/09/17.
 */

public class Navigator {
    public static void goToMain(AppCompatActivity activity, View view) {
        ActivityOptionsCompat aoc;
        if (AndroidVersions.isMarshmallow() && view != null) {
            aoc = ActivityOptionsCompat.makeClipRevealAnimation(view,
                    0,
                    0,
                    view.getMeasuredWidth(),
                    view.getMeasuredHeight());
        } else {
            aoc = ActivityOptionsCompat.makeBasic();
        }
        Intent intent = new Intent(activity, MainActivity.class);
        ActivityCompat.startActivity(activity, intent, aoc.toBundle());
        ActivityCompat.finishAffinity(activity);
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
}
