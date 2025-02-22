package com.maddog05.whatanime.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.maddog05.maddogutilities.android.Permissions
import com.maddog05.maddogutilities.string.Strings
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.entity.SearchImageResult
import com.maddog05.whatanime.core.image.GlideLoader
import com.maddog05.whatanime.core.image.KImageUtil
import com.maddog05.whatanime.core.mvp.presenter.MainPresenter
import com.maddog05.whatanime.core.mvp.view.MainView
import com.maddog05.whatanime.databinding.ActivityMainTwoBinding
import com.maddog05.whatanime.ui.adapter.AdapterMain
import com.maddog05.whatanime.ui.dialog.ChangelogDialog
import com.maddog05.whatanime.ui.dialog.InputUrlDialog
import com.maddog05.whatanime.ui.dialog.SearchResultInfoDialog
import com.maddog05.whatanime.ui.tor.Navigator
import com.maddog05.whatanime.util.C
import com.maddog05.whatanime.util.Mapper
import com.maddog05.whatanime.util.storage.StorageModel
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity(R.layout.activity_main_two), MainView {

    companion object {
        private const val PERMISSIONS_SELECT_IMAGE = 101
    }

    private lateinit var binding: ActivityMainTwoBinding

    private lateinit var presenter: MainPresenter
    private var currentBitmap: Bitmap? = null
    private var isSearchRunning = false

    private val requestPhotoActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val notNullData = result.data!!
                if (notNullData.data != null) {
                    val bitmap = KImageUtil.getExternalImageAsBitmap(this, notNullData.data!!)
                    if (bitmap != null)
                        processBitmap(bitmap)
                    else
                        showErrorGeneric(getString(R.string.error_image_recovered_from_storage))
                }
            } else {
                showErrorGeneric(getString(R.string.error_image_recovered_from_storage))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = MainPresenter(this)
        setSupportActionBar(binding.toolbarMain)
        binding.rvMainResults.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvMainResults.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.ivMainPhoto.setOnClickListener {
            if (!isSearchRunning)
                actionSelectImage()
        }
        binding.fabMainSearch.setOnClickListener {
            if (!isSearchRunning)
                presenter.actionSearch()
        }
        binding.tvMainSearchPerMinute.setOnClickListener {
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

    override fun onDestroy() {
        super.onDestroy()
        val storageModel = StorageModel(applicationContext)
        val file = File(storageModel.getTempImageFolder(), "whatanime_temp_search.png")
        if (file.exists())
            file.delete()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_SELECT_IMAGE) {
            if (Permissions.isPermissionGranted(grantResults)) {
                actionSelectImageExecute()
            } else {
                showErrorGeneric(getString(R.string.error_permissions_denied))
            }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun actionCheckSendIntent() {
        val intent = intent
        val action = intent.action ?: ""
        val type = intent.type ?: ""
        if (action.isNotEmpty() && action == Intent.ACTION_SEND && type.isNotEmpty()) {
            if (type.startsWith("image/")) {
                val prevUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                val uri = prevUri?.toString() ?: ""
                if (uri.isNotEmpty()) {
                    val glideLoader = GlideLoader.create()
                    glideLoader.with(this)
                    glideLoader.load(uri)
                    glideLoader.loadAsBitmap { bitmap ->
                        processBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun actionShowQuotaInfo() {
        AlertDialog.Builder(this)
            .setMessage(R.string.indicator_quota_info)
            .setCancelable(false)
            .setPositiveButton(R.string.action_ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun actionSelectImage() {
        val checker = Permissions.Checker(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checker.addPermission(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            checker.addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (checker.isPermissionsGranted) {
            actionSelectImageExecute()
        } else {
            ActivityCompat.requestPermissions(
                this,
                checker.permissionsToRequest,
                PERMISSIONS_SELECT_IMAGE
            )
        }
    }

    private fun actionSelectImageExecute() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_select_source)
            .setItems(R.array.array_main_image_source_options) { dialog, which ->
                dialog?.dismiss()
                when (which) {
                    0 -> sourceImage()
                    1 -> sourceUrl()
                }
            }
            .setNegativeButton(R.string.action_close) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun processBitmap(bitmap: Bitmap?) {
        if (bitmap != null) {
            currentBitmap = bitmap
            binding.ivMainPhoto.setImageBitmap(bitmap)
        } else
            showErrorGeneric(getString(R.string.error_image_recovered_from_storage))
    }

    private fun sourceUrl() {
        val dialog = InputUrlDialog(this)
        dialog.setCallback { url ->
            if (Strings.isStringUrl(url)) {
                presenter.actionSearchWithUrl(url)
            } else
                showErrorGeneric(getString(R.string.error_url_invalid))
        }
        dialog.show()
    }

    private fun sourceImage() {
        requestPhotoActivityResult.launch(Navigator.getIntentSelectImage(this))
    }

    private fun showErrorGeneric(text: String) {
        Toasty.error(this, text, Toast.LENGTH_SHORT).show()
    }

    //MVP
    override fun mvpContext(): Context {
        return applicationContext
    }

    override fun drawSearchResults(results: MutableList<SearchImageResult>) {
        binding.rvMainResults.adapter =
            AdapterMain(applicationContext, results, object : AdapterMain.OnDocClickListener {
                override fun onDocClicked(doc: SearchImageResult) {
                    showDocDetail(doc)
                }
            })
    }

    private fun showDocDetail(doc: SearchImageResult) {
        SearchResultInfoDialog()
            .withDoc(doc)
            .withListener(object : SearchResultInfoDialog.OnSearchResultOptionListener {
                override fun onShareText() {
                    val nameAndEpisodeText =
                        Mapper.parseEpisodeNumber(this@MainActivity, doc.episode)
                    val title = doc.filename
                    val text = (title
                            + C.SPACE
                            + nameAndEpisodeText
                            + C.SPACE
                            + getString(R.string.share_founded_with)
                            + C.SPACE
                            + getString(R.string.app_name))
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, text)
                    startActivity(Intent.createChooser(intent, getString(R.string.action_share)))
                }

                override fun onShowSample() {
                    Navigator.goToPreviewVideo(this@MainActivity, doc.videoUrl, doc)
                }
            })
            .show(supportFragmentManager, "docDetail")
    }

    override fun getInputFile(): File? {
        if (currentBitmap != null) {
            val storageModel = StorageModel(applicationContext)
            val file = File(storageModel.getTempImageFolder(), "whatanime_temp_search.png")
            if (file.exists())
                file.delete()
            file.createNewFile()
            return try {
                val fos = FileOutputStream(file)
                currentBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.close()
                file
            } catch (e: Exception) {
                null
            }
        } else return null
    }

    override fun setSearchPerMinute(number: Int) {
        binding.tvMainSearchPerMinute.text =
            getString(R.string.input_search_per_minute, number.toString())
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
        binding.tvMainTutorial.visibility = if (wantVisible) View.VISIBLE else View.GONE
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private var snackbar: Snackbar? = null

    @SuppressLint("WakelockTimeout", "Wakelock")
    override fun showLoading(wantVisible: Boolean) {
        isSearchRunning = wantVisible
        if (wantVisible) {
            snackbar = Snackbar.make(
                binding.fabMainSearch,
                R.string.indicator_searching_anime,
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar?.show()
            wakeLock =
                (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WhatAnime::MainWakeLock").apply {
                        acquire()
                    }
                }
        } else {
            snackbar?.dismiss()
            snackbar = null
            if (wakeLock != null && wakeLock!!.isHeld)
                wakeLock?.release()
        }
    }
}