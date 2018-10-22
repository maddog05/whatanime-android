package com.maddog05.whatanime.core.mvp.presenter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.maddog05.maddogutilities.callback.Callback
import com.maddog05.maddogutilities.image.ImageEncoder
import com.maddog05.maddogutilities.image.Images
import com.maddog05.maddogutilities.string.Strings
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.Logic
import com.maddog05.whatanime.core.LogicApp
import com.maddog05.whatanime.core.entity.RequestEntity
import com.maddog05.whatanime.core.mvp.view.HomeView
import com.maddog05.whatanime.ui.adapter.AdapterHome
import com.maddog05.whatanime.ui.dialog.ChangelogDialog
import com.maddog05.whatanime.ui.dialog.InputUrlDialog
import com.maddog05.whatanime.ui.tor.Filetor
import com.maddog05.whatanime.ui.tor.Navigator
import com.maddog05.whatanime.util.C
import com.maddog05.whatanime.util.Mapper
import com.maddog05.whatanime.util.Times
import ru.whalemare.sheetmenu.SheetMenu

class HomePresenter(val view: HomeView) {
    //https://github.com/zhihu/Matisse
    companion object {
        const val REQUEST_PHOTO_GALLERY = 102
        const val REQUEST_VIDEO_GALLERY = 103
        const val REQUEST_FRAME_VIDEO = 104
        const val REQUEST_OPEN_SETTINGS = 105
    }

    private val logic: LogicApp = Logic.get(view.mvpContext())

    private lateinit var adapter: AdapterHome

    fun onCreate() {
        view.setupViews()
        view.setupActions()
        adapter = AdapterHome(view.mvpContext())
        adapter.setClickCallback(Callback { request ->
            processClickDone(request)
        })
        adapter.setClickAction(Callback { request -> processReload(request) })
        view.getRecyclerView().adapter = adapter
        setupData()
        if (!logic.isChangelogViewed) {
            showChangelog()
            logic.finishChangelogViewed()
        }
        val intent = view.mvpIntent()
        val action = intent.action ?: ""
        val type = intent.type ?: ""
        if (action.isNotEmpty() && action == Intent.ACTION_SEND && type.isNotEmpty()) {
            if (type.startsWith("image/")) {
                processShareImage(intent)
            }
        }
    }

    private fun setupData() {
        val requests = logic.databaseGetAllRequests()
        adapter.setupData(requests)
        view.showNoItems(requests.isEmpty())
        if (requests.size > 0) {
            view.getRecyclerView().scrollToPosition(requests.size - 1)
        }
    }

    private fun processShareImage(intent: Intent) {
        val uri = (intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri).toString()
        logic.loadImageUrl(uri) { bitmap ->
            processBitmap(bitmap)
        }
    }

    fun showMenuSources() {
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
        }.show(view.mvpContext())

    }

    private fun sourceImage() {
        view.mvpStartActivityForResult(
                Navigator.getIntentSelectImage(view.mvpContext()), REQUEST_PHOTO_GALLERY)
    }

    private fun sourceVideo() {
        view.mvpStartActivityForResult(
                Navigator.getIntentSelectVideo(view.mvpContext()), REQUEST_VIDEO_GALLERY)
    }

    private fun sourceUrl() {
        val dialog = InputUrlDialog(view.mvpContext())
        dialog.setCallback(Callback { url ->
            if (Strings.isStringUrl(url)) {
                loadPhotoUrl(url)
            } else {
                view.showError(view.mvpContext().getString(R.string.error_url_invalid))
            }
        })
        dialog.show()
    }

    private fun loadPhotoUrl(url: String) {
        view.showLoading(view.mvpContext().getString(R.string.indicator_loading_image))
        logic.loadImageUrl(url) { bitmap ->
            processBitmap(bitmap)
            view.hideLoading()
        }
    }

    fun processOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PHOTO_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val pair = Images.getOutputPhotoGalleryCompressed(view.mvpContext(), data, 512)
                    val bitmap = pair.bitmap
                    if (bitmap != null)
                        processBitmap(bitmap)
                    else
                        view.showError(view.mvpContext().getString(R.string.error_image_recovered_from_storage))
                } else {
                    view.showError(view.mvpContext().getString(R.string.error_image_recovered_from_storage))
                }
            }
            REQUEST_VIDEO_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri = data.data
                    val path = Mapper.parseLocalVideoPath(view.mvpContext(), uri)
                    if (path != null) {
                        Navigator.goToSelectVideo(view.mvpContext() as AppCompatActivity, path, REQUEST_FRAME_VIDEO)
                    } else {
                        view.showError(view.mvpContext().getString(R.string.error_video_recovered_from_storage))
                    }
                }
            }
            REQUEST_FRAME_VIDEO -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val bitmap = Mapper.parseVideoFrameFromSelectFrame(data)
                    if (bitmap != null)
                        processBitmap(bitmap)
                }
            }
            REQUEST_OPEN_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val b1 = data.extras
                    val isClearHistory = b1.getBoolean(C.Extras.SETTING_CHANGE_CLEAR_HISTORY, false)
                    if (isClearHistory)
                        setupData()
                }
            }
        }
    }

    private fun processBitmap(bitmap: Bitmap) {
        val request = RequestEntity()
        request.status = RequestEntity.STATUS_REQUESTING
        request.date = Times.now()

        val id = logic.databaseCreateRequest(request)
        request.id = id

        val fileName = Filetor.saveSampleImageLocal(bitmap,
                C.ROOT_IMAGES + "/" + C.FOLDER_REQUESTS,
                Filetor.getRequestImageLocalName(id))
        request.imageUrl = fileName
        logic.databaseUpdateRequest(request)
        adapter.addRequest(request)
        val count = adapter.itemCount
        if (count > 0)
            view.getRecyclerView().scrollToPosition(count - 1)
        view.showNoItems(false)
        search(bitmap, request)
    }

    private fun search(bitmap: Bitmap, request: RequestEntity) {
        ImageEncoder.with(bitmap)
                .callback { encoded ->
                    logic.searchAnime(encoded, "") { responseServer ->
                        if (responseServer.errorMessage.isEmpty()) {
                            request.status = RequestEntity.STATUS_DONE
                            val responses = Mapper.parseResponseEntity(responseServer.searchDetail.docs)
                            logic.databaseSetResponses(request.id, responses)
                        } else {
                            request.status = RequestEntity.STATUS_INCOMPLETED
                            view.showError(responseServer.errorMessage)
                        }
                        logic.databaseUpdateRequest(request)
                        if (request.status == RequestEntity.STATUS_DONE) {
                            val modifiedPosition = adapter.updateRequest(logic.databaseGetRequest(request.id))
                            if (modifiedPosition > -1)
                                view.getRecyclerView().smoothScrollToPosition(modifiedPosition)
                        }
                    }
                }
                .encode()
    }

    private fun processClickDone(request: RequestEntity) {
        if (request.status == RequestEntity.STATUS_DONE) {
            val intent = Intent(view.mvpContext(), C.UI_SEARCH_RESULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val bundle = Bundle()
            bundle.putLong(C.Extras.ID_REQUEST, request.id)
            intent.putExtras(bundle)
            view.mvpStartActivity(intent)
        }
    }

    private fun processReload(request: RequestEntity) {
        logic.loadImageUrl(request.imageUrl) { bitmap ->
            search(bitmap, request)
        }
    }

    private fun showChangelog() {
        ChangelogDialog.newInstance(view.mvpContext() as AppCompatActivity).showDialog()
    }

    fun openSettings() {
        val intent = Navigator.getIntentSettings(view.mvpContext())
        view.mvpStartActivityForResult(intent, REQUEST_OPEN_SETTINGS)
    }

}