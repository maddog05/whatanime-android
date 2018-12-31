package com.maddog05.whatanime.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.maddog05.maddogutilities.android.Permissions
import com.maddog05.maddogutilities.callback.Callback
import com.maddog05.maddogutilities.image.Images
import com.maddog05.maddogutilities.string.Strings
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.entity.SearchDetail
import com.maddog05.whatanime.core.image.GlideLoader
import com.maddog05.whatanime.core.mvp.presenter.MainPresenter
import com.maddog05.whatanime.core.mvp.view.MainView
import com.maddog05.whatanime.ui.adapter.AdapterMain
import com.maddog05.whatanime.ui.dialog.ChangelogDialog
import com.maddog05.whatanime.ui.dialog.InputUrlDialog
import com.maddog05.whatanime.ui.dialog.QuotaInfoDialog
import com.maddog05.whatanime.ui.tor.Navigator
import com.maddog05.whatanime.util.Mapper
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main_two.*
import ru.whalemare.sheetmenu.SheetMenu

class MainActivity : AppCompatActivity(), MainView {

    companion object {
        private const val PERMISSIONS_SELECT_IMAGE = 101
        private const val REQUEST_PHOTO_GALLERY = 102
        private const val REQUEST_VIDEO_GALLERY = 103
        private const val REQUEST_FRAME_VIDEO = 104
    }

    private lateinit var presenter: MainPresenter
    private var currentBitmap: Bitmap? = null
    private var isSearchRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_two)
        presenter = MainPresenter(this)
        setSupportActionBar(toolbar)
        rv_main_results.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        iv_main_photo.setOnClickListener {
            if (!isSearchRunning)
                actionSelectImage()
        }
        fab_main_search.setOnClickListener {
            if (!isSearchRunning)
                presenter.actionSearch()
        }
        btn_main_info_quota.setOnClickListener {
            if (!isSearchRunning)
                actionShowQuotaInfo()
        }
        actionCheckSendIntent()
        presenter.onCreate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_settings) {
            if (!isSearchRunning) {
                val intent = Navigator.getIntentSettings(this)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_SELECT_IMAGE) {
            if (Permissions.isPermissionGranted(grantResults)) {
                actionSelectImageExecute()
            } else {
                showErrorGeneric(getString(R.string.error_permissions_denied))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PHOTO_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val pair = Images.getOutputPhotoGalleryCompressed(this, data, 512)
                    val bitmap = pair.bitmap
                    if (bitmap != null)
                        processBitmap(bitmap)
                    else
                        showErrorGeneric(getString(R.string.error_image_recovered_from_storage))
                } else {
                    showErrorGeneric(getString(R.string.error_image_recovered_from_storage))
                }
            }
            REQUEST_VIDEO_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri = data.data
                    val path = Mapper.parseLocalVideoPath(this, uri)
                    if (path != null) {
                        Navigator.goToSelectVideo(this, path, REQUEST_FRAME_VIDEO)
                    } else {
                        showErrorGeneric(getString(R.string.error_video_recovered_from_storage))
                    }
                }
            }
            REQUEST_FRAME_VIDEO -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val bitmap = Mapper.parseVideoFrameFromSelectFrame(data)
                    if (bitmap != null)
                        processBitmap(bitmap)
                    else
                        showErrorGeneric(getString(R.string.error_image_recovered_from_storage))
                }
            }
        }
    }

    private fun actionCheckSendIntent() {
        val intent = intent
        val action = intent.action ?: ""
        val type = intent.type ?: ""
        if (action.isNotEmpty() && action == Intent.ACTION_SEND && type.isNotEmpty()) {
            if (type.startsWith("image/")) {
                val uri = (intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri).toString()
                val glideLoader = GlideLoader.create()
                glideLoader.with(this)
                glideLoader.load(uri)
                glideLoader.loadAsBitmap { bitmap ->
                    processBitmap(bitmap)
                }
            }
        }
    }

    private fun actionShowQuotaInfo() {
        QuotaInfoDialog.newInstance(this)
                .showDialog()
    }

    private fun actionSelectImage() {
        val checker = Permissions.Checker(this)
                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checker.isPermissionsGranted) {
            actionSelectImageExecute()
        } else {
            ActivityCompat.requestPermissions(this, checker.permissionsToRequest, PERMISSIONS_SELECT_IMAGE)
        }
    }

    private fun actionSelectImageExecute() {
        SheetMenu().apply {
            titleId = R.string.title_select_source
            menu = R.menu.menu_home_search_options

            click = MenuItem.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_home_image -> sourceImage()
                    R.id.menu_home_video -> sourceVideo()
                    R.id.menu_home_url -> sourceUrl()
                }
                true
            }
        }.show(this)
    }

    private fun processBitmap(bitmap: Bitmap?) {
        if (bitmap != null) {
            currentBitmap = bitmap
            iv_main_photo.setImageBitmap(bitmap)
        } else
            showErrorGeneric(getString(R.string.error_image_recovered_from_storage))
    }

    private fun sourceUrl() {
        val dialog = InputUrlDialog(this)
        dialog.setCallback(Callback { url ->
            if (Strings.isStringUrl(url)) {
                val glideLoader = GlideLoader.create()
                glideLoader.with(this)
                glideLoader.load(url)
                glideLoader.loadAsBitmap { bitmap ->
                    processBitmap(bitmap)
                }
            } else
                showErrorGeneric(getString(R.string.error_url_invalid))
        })
    }

    private fun sourceVideo() {
        startActivityForResult(Navigator.getIntentSelectVideo(this), REQUEST_VIDEO_GALLERY)
    }

    private fun sourceImage() {
        startActivityForResult(Navigator.getIntentSelectImage(this), REQUEST_PHOTO_GALLERY)
    }

    private fun showErrorGeneric(text: String) {
        Toasty.error(this, text, Toast.LENGTH_SHORT).show()
    }

    //MVP
    override fun mvpContext(): Context {
        return applicationContext
    }

    override fun drawSearchResults(results: MutableList<SearchDetail.Doc>) {
        rv_main_results.adapter = AdapterMain(applicationContext, results, object : AdapterMain.OnDocClickListener {
            override fun onDocClicked(doc: SearchDetail.Doc) {
                val url = Mapper.getVideoUrl(doc)
                Navigator.goToPreviewVideo(this@MainActivity, url, doc)
            }
        })
    }

    override fun getInputBitmap(): Bitmap? {
        return currentBitmap
    }

    override fun setSearchQuota(number: Int) {
        tv_main_search_quota.text = getString(R.string.input_search_quota, number.toString())
    }

    override fun setSearchPerMinute(number: Int) {
        tv_main_search_per_minute.text = getString(R.string.input_search_per_minute, number.toString())
    }

    override fun showChangelog() {
        ChangelogDialog.newInstance(this)
                .showDialog()
    }

    override fun showErrorImageEmpty() {
        showErrorGeneric(getString(R.string.error_image_not_selected))
    }

    override fun showErrorInternet() {
        showErrorGeneric(getString(R.string.error_internet_connection))
    }

    override fun showErrorServer(text: String) {
        showErrorGeneric(text)
    }

    override fun showIndicatorSearchResults(wantVisible: Boolean) {
        tv_main_tutorial.visibility = if (wantVisible) View.VISIBLE else View.GONE
    }

    private var wakeLock: PowerManager.WakeLock? = null

    override fun showLoading(wantVisible: Boolean) {
        isSearchRunning = wantVisible
        layout_main_loading.visibility = if (wantVisible) View.VISIBLE else View.GONE
        if (wantVisible) {
            fab_main_search.hide()
            wakeLock =
                    (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                        newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WhatAnime::MainWakeLock").apply {
                            acquire()
                        }
                    }
        } else {
            if (wakeLock != null && wakeLock!!.isHeld)
                wakeLock?.release()
            fab_main_search.show()
        }
    }
}