package com.maddog05.whatanime.ui.tor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.maddog05.whatanime.R;
import com.maddog05.whatanime.core.entity.output.SearchDetail;
import com.maddog05.whatanime.ui.activity.InformationActivity;
import com.maddog05.whatanime.ui.activity.VideoPreviewActivity;
import com.maddog05.whatanime.ui.activity.VideoSelectActivity;
import com.maddog05.whatanime.ui.activity.SettingsActivity;
import com.maddog05.whatanime.util.C;

/*
 * Created by andreetorres on 27/09/17.
 */

public class Navigator {
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
