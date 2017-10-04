package com.maddog05.whatanime.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.maddog05.maddogutilities.android.Checkers;
import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.maddogutilities.image.Images;
import com.maddog05.maddogutilities.view.SquareImageView;
import com.maddog05.whatanime.App;
import com.maddog05.whatanime.R;
import com.maddog05.whatanime.core.Logic;
import com.maddog05.whatanime.core.LogicApp;
import com.maddog05.whatanime.core.entity.SearchAnimeResponse;
import com.maddog05.whatanime.core.entity.SearchDetail;
import com.maddog05.whatanime.ui.adapter.AdapterSearchResults;
import com.maddog05.whatanime.ui.tor.Animator;
import com.maddog05.whatanime.ui.tor.Navigator;
import com.maddog05.whatanime.ui.tor.Tutorator;
import com.maddog05.whatanime.util.C;
import com.maddog05.whatanime.util.Mapper;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //APP LOGIC
    LogicApp logic;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navview;

    private SquareImageView photoIv;
    private FloatingActionButton searchFab;
    private BottomSheetBehavior resultsBottom;
    private AppCompatImageButton closeBottomBtn;
    private RecyclerView resultsRv;
    private LinearLayout indicatorLoadingLayout;
    private AppCompatTextView indicatorStatusTv;

    private static final int REQUEST_PICTURE_GALLERY = 102;
    private Bitmap bitmap = null;
    private String pathPrevious = C.EMPTY;
    private String pathToSearch = C.EMPTY;

    private SearchDetail searchDetail = null;

    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logic = Logic.get(MainActivity.this);
        setupToolbar();
        setupViews();
        setupActions();
        setupWakeLock();
        showTutorial();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICTURE_GALLERY) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Images.OutputPhoto pair = Images.getOutputPhotoGalleryCompressed(MainActivity.this, data, 512);
                    pathToSearch = pair.path;
                    bitmap = pair.bitmap;
                    setupPhoto(pair.bitmap);
                } else {
                    showError(getString(R.string.error_image_recovered_from_storage));
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpened())
            closeDrawer();
        else if (resultsBottom.getState() == BottomSheetBehavior.STATE_EXPANDED)
            resultsBottom.setState(BottomSheetBehavior.STATE_HIDDEN);
        else
            super.onBackPressed();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupViews() {
        drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        navview = (NavigationView) findViewById(R.id.navview);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.main_drawer_open, R.string.main_drawer_close);
        //drawer.addDrawerListener(this);
        toggle.syncState();
        navview.setNavigationItemSelectedListener(this);

        View _view = findViewById(R.id.bottom_search_results);
        resultsBottom = BottomSheetBehavior.from(_view);
        resultsBottom.setHideable(true);
        resultsBottom.setState(BottomSheetBehavior.STATE_HIDDEN);
        resultsRv = (RecyclerView) findViewById(R.id.rv_results);
        resultsRv.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.VERTICAL, false));
        photoIv = (SquareImageView) findViewById(R.id.iv_photo);
        searchFab = (FloatingActionButton) findViewById(R.id.fab_search);
        closeBottomBtn = (AppCompatImageButton) findViewById(R.id.btn_close_bottom);
        indicatorLoadingLayout = (LinearLayout) findViewById(R.id.layout_main_loading);
        indicatorStatusTv = (AppCompatTextView) findViewById(R.id.tv_progress_status);
        AppCompatTextView versionAppTv = navview.getHeaderView(0).findViewById(R.id.tv_header_app_version);
        versionAppTv.setText(App.getAppVersion(MainActivity.this));
    }

    private void setupActions() {
        closeBottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultsBottom.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        resultsBottom.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    searchFab.setEnabled(false);
                } else {
                    searchFab.setEnabled(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                searchFab.setAlpha(1 - slideOffset);
            }
        });
        photoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isProcessRunning())
                    selectImage();
            }
        });
        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isProcessRunning())
                    searchAnime();
            }
        });
    }

    private void setupWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "searchingAnimeWakeLock");
    }

    private void showTutorial() {
        if (logic.isFirstTutorial()) {
            _showTutorial();
            logic.finishFirstTutorial();
        }
    }

    private void _showTutorial() {
        Tutorator.with(MainActivity.this)
                .addViewString(photoIv, getString(R.string.tutorial_one))
                .addViewString(searchFab, getString(R.string.tutorial_two))
                .start();
    }

    private boolean isDrawerOpened() {
        return drawer.isDrawerOpen(navview);
    }

    private void closeDrawer() {
        drawer.closeDrawers();
    }

    private void setupPhoto(Bitmap bitmap) {
        photoIv.setImageBitmap(bitmap);
    }

    private void showInfo(String text) {
        Toasty.info(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void showError(String text) {
        Toasty.error(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private boolean isProcessRunning() {
        return indicatorLoadingLayout.getVisibility() == View.VISIBLE;
    }

    private void showLoadingIndicator(String message) {
        if (!isProcessRunning()) {
            indicatorLoadingLayout.setVisibility(View.VISIBLE);
            hideFab();
        }
        indicatorStatusTv.setText(message);
    }

    private void hideLoadingIndicator() {
        if (isProcessRunning()) {
            indicatorLoadingLayout.setVisibility(View.GONE);
        }
        showFab();
    }

    private void showFab() {
        searchFab.setVisibility(View.VISIBLE);
        searchFab.startAnimation(Animator.showFab(MainActivity.this));
    }

    private void hideFab() {
        searchFab.setVisibility(View.INVISIBLE);
        searchFab.startAnimation(Animator.hideFab(MainActivity.this));
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooser = Intent.createChooser(intent, getString(R.string.indicator_choose_gallery_app));
        startActivityForResult(chooser, REQUEST_PICTURE_GALLERY);
    }

    private void searchAnime() {
        if (bitmap == null) {
            showError(getString(R.string.error_photo_required));
        } else if (pathPrevious.equals(pathToSearch) && searchDetail != null) {
            drawResults(searchDetail);
        } else if (!Checkers.isInternetAvailable(MainActivity.this)) {
            showError(getString(R.string.error_internet_connection));
        } else {
            showLoadingIndicator(getString(R.string.indicator_encoding_image));
            new Images.EncodeBitmapBase64AsyncTask(bitmap) {
                @Override
                protected void onPostExecute(String encoded) {
                    if (encoded == null || encoded.isEmpty()) {
                        hideLoadingIndicator();
                        showError(getString(R.string.error_encoding_image));
                    } else {
                        _searchAnime(encoded);
                    }
                }
            }.execute();
        }
    }

    private void _searchAnime(String encoded) {
        if (Checkers.isInternetAvailable(MainActivity.this)) {
            wakeLock.acquire();
            showLoadingIndicator(getString(R.string.indicator_searching_anime));
            logic.searchAnime(encoded, new Callback<SearchAnimeResponse>() {
                @Override
                public void done(SearchAnimeResponse response) {
                    hideLoadingIndicator();
                    wakeLock.release();
                    if (response.errorMessage.isEmpty()) {
                        if (response.searchDetail.docs.size() > 0) {
                            searchDetail = response.searchDetail;
                            drawResults(searchDetail);
                            pathPrevious = pathToSearch;
                        } else {
                            showInfo(getString(R.string.error_no_results_founded));
                        }
                    } else {
                        showError(response.errorMessage);
                    }
                }
            });
        } else {
            showError(getString(R.string.error_internet_connection));
        }
    }

    private void drawResults(SearchDetail data) {
        AdapterSearchResults adapter = new AdapterSearchResults(MainActivity.this);
        adapter.setDocs(data.docs);
        adapter.setCallbackItemClick(new Callback<SearchDetail.Doc>() {
            @Override
            public void done(SearchDetail.Doc doc) {
                processClickDoc(doc);
            }
        });
        resultsRv.setAdapter(adapter);
        resultsBottom.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void processClickDoc(SearchDetail.Doc doc) {
        String url = Mapper.getVideoUrl(doc);
        Navigator.goToPreviewVideo(MainActivity.this, url, doc);
    }

    //NAVVIEW ITEM SELECTED
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_tutorial:
                closeDrawer();
                _showTutorial();
                break;
            case R.id.menu_item_information:
                Navigator.goToInformation(MainActivity.this);
        }
        return false;
    }
}
