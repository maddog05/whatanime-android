package com.maddog05.whatanime.ui;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.maddog05.maddogutilities.android.Permissions;
import com.maddog05.whatanime.R;
import com.maddog05.whatanime.ui.tor.Animator;
import com.maddog05.whatanime.ui.tor.Navigator;

import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class SplashActivity extends AppCompatActivity {

    private AppCompatImageView logoIv;
    private AppCompatTextView indicatorPermissionsTv;
    private AppCompatButton requestPermissionsBtn;

    private static final int REQUEST_CODE = 101;
    private String[] permissions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setupViews();
        setupActions();
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (Permissions.isPermissionGranted(grantResults)) {
                goToMain(true);
            } else {
                showError(getString(R.string.error_permissions_denied));
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setupViews() {
        logoIv = (AppCompatImageView) findViewById(R.id.iv_logo);
        indicatorPermissionsTv = (AppCompatTextView) findViewById(R.id.tv_indicator_permissions);
        requestPermissionsBtn = (AppCompatButton) findViewById(R.id.btn_request_permissions);
    }

    private void setupActions() {
        requestPermissionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissions != null) {
                    ActivityCompat.requestPermissions(SplashActivity.this,
                            permissions,
                            REQUEST_CODE);
                }
            }
        });
    }

    private void showError(String text) {
        Toasty.error(SplashActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void checkPermissions() {
        Permissions.Checker checker = new Permissions.Checker(SplashActivity.this);
        checker.addPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        checker.addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean isPermissionsGranted = checker.isPermissionsGranted();
        if (isPermissionsGranted) {
            goToMain(false);
        } else {
            permissions = checker.getPermissionsToRequest();
            showIndicatorPermissions();
        }
    }

    private void showIndicatorPermissions() {
        indicatorPermissionsTv.setVisibility(View.VISIBLE);
        requestPermissionsBtn.setVisibility(View.VISIBLE);
    }

    private void hideIndicatorPermissions() {
        indicatorPermissionsTv.setVisibility(View.INVISIBLE);
        indicatorPermissionsTv.startAnimation(Animator.hideAnimation());
        requestPermissionsBtn.setVisibility(View.INVISIBLE);
        requestPermissionsBtn.startAnimation(Animator.hideAnimation());
    }

    private void goToMain(boolean executeAnimation) {
        if (executeAnimation) {
            hideIndicatorPermissions();
        } else {
            indicatorPermissionsTv.setVisibility(View.INVISIBLE);
            requestPermissionsBtn.setVisibility(View.INVISIBLE);
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                _goToMain();
            }
        };
        new Timer().schedule(timerTask, 1000);
    }

    private void _goToMain() {
        Navigator.goToMain(SplashActivity.this, logoIv);
    }
}
