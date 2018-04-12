package com.maddog05.whatanime.ui;

import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.maddog05.maddogutilities.android.Checkers;
import com.maddog05.whatanime.R;
import com.maddog05.whatanime.core.entity.SearchDetail;
import com.maddog05.whatanime.ui.tor.Navigator;
import com.maddog05.whatanime.util.C;

import es.dmoral.toasty.Toasty;

public class VideoPreviewActivity extends AppCompatActivity implements OnPreparedListener, OnCompletionListener {
    private VideoView videoView;
    private AppCompatImageButton shareBtn, expandBtn;
    private ProgressBar loadingPbar;

    private String videoUrl = C.EMPTY;
    private SearchDetail.Doc doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        setupExtraData();
        setupToolbar();
        setupViews();
        setupActions();
        setupData();
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViews() {
        shareBtn = findViewById(R.id.btn_share);
        expandBtn = findViewById(R.id.btn_expand);
        loadingPbar = findViewById(R.id.pbar_loading);
        videoView = findViewById(R.id.video_view_preview);
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
    }

    private void setupActions() {
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _share();
            }
        });
        expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _expand();
            }
        });
    }

    private void setupExtraData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            videoUrl = bundle.getString(C.Extras.VIDEO_URL, C.EMPTY);
            doc = bundle.getParcelable(C.Extras.DOC);
        }
    }

    private void setupData() {
        String title = doc.romanjiTitle != null && !doc.romanjiTitle.isEmpty() ? doc.romanjiTitle : doc.anime;
        setupTitle(title);
        if (Checkers.isInternetInWifiOrData(VideoPreviewActivity.this)) {
            videoView.setVideoURI(Uri.parse(videoUrl));
        } else {
            showError(getString(R.string.error_internet_connection));
        }
    }

    private void showError(String text) {
        Toasty.error(VideoPreviewActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void setupTitle(String text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    private void _expand() {
        View decorView = getWindow().getDecorView();
        if (decorView != null) {
            boolean isNotFullScreen = decorView.getSystemUiVisibility() == View.SYSTEM_UI_FLAG_VISIBLE;
            if (isNotFullScreen) {
                goFullscreen();
            } else {
                exitFullscreen();
            }
        }
    }

    private void _share() {
        String title = doc.romanjiTitle != null && !doc.romanjiTitle.isEmpty() ? doc.romanjiTitle : doc.anime;
        String text = title
                + C.SPACE
                + doc.episode
                + C.SPACE
                + getString(R.string.share_founded_with)
                + C.SPACE
                + getString(R.string.app_name);
        Navigator.shareText(VideoPreviewActivity.this, text);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exitFullscreen();
    }

    private void goFullscreen() {
        setUiFlags(true);
    }

    private void exitFullscreen() {
        setUiFlags(false);
    }

    private void setUiFlags(boolean fullscreen) {
        View decorView = getWindow().getDecorView();
        if (decorView != null) {
            decorView.setSystemUiVisibility(fullscreen ? getFullscreenUiFlags() : View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    private int getFullscreenUiFlags() {
        int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        return flags;
    }

    @Override
    public void onPrepared() {
        loadingPbar.setVisibility(View.GONE);
        videoView.start();
    }

    @Override
    public void onCompletion() {
        loadingPbar.setVisibility(View.GONE);
        videoView.restart();
    }

    //BACK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
