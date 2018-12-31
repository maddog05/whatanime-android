package com.maddog05.whatanime.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.Toast
import com.maddog05.maddogutilities.android.Permissions
import com.maddog05.maddogutilities.callback.Callback
import com.maddog05.maddogutilities.image.Images
import com.maddog05.maddogutilities.string.Strings
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.image.GlideLoader
import com.maddog05.whatanime.ui.dialog.InputUrlDialog
import com.maddog05.whatanime.ui.tor.Navigator
import com.maddog05.whatanime.util.Mapper
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main_two.*
import ru.whalemare.sheetmenu.SheetMenu

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSIONS_SELECT_IMAGE = 101
        private const val REQUEST_PHOTO_GALLERY = 102
        private const val REQUEST_VIDEO_GALLERY = 103
        private const val REQUEST_FRAME_VIDEO = 104
    }

    private var currentBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_two)
        setSupportActionBar(toolbar)
        rv_main_results.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tv_main_tutorial
        iv_main_photo.setOnClickListener { actionSelectImage() }
        fab_main_search.setOnClickListener { }
        btn_main_info_quota.setOnClickListener { }
        tv_main_search_quota
        tv_main_search_per_minute
        layout_main_loading
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
}