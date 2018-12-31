package com.maddog05.whatanime.core.mvp.presenter

import com.maddog05.maddogutilities.android.Checkers
import com.maddog05.maddogutilities.image.ImageEncoder
import com.maddog05.whatanime.BuildConfig
import com.maddog05.whatanime.core.data.LogicPreferenceSharedPref
import com.maddog05.whatanime.core.entity.SearchDetail
import com.maddog05.whatanime.core.mvp.view.MainView
import com.maddog05.whatanime.core.network.LogicNetworkRetrofit

class MainPresenter(private val view: MainView) {

    private val network = LogicNetworkRetrofit.newInstance()
    private val preferences = LogicPreferenceSharedPref.newInstance(view.mvpContext())
    private val docs = mutableListOf<SearchDetail.Doc>()
    private var searchQuota = 0
    private var searchPerMinute = 0

    fun onCreate() {
        view.showIndicatorSearchResults(true)
        view.showLoading(false)
        val currentVersion = BuildConfig.VERSION_CODE
        val isChangelogViewed = preferences.lastChangelogVersion == currentVersion
        if (!isChangelogViewed) {
            view.showChangelog()
            preferences.lastChangelogVersion = currentVersion
        }
        getQuota()
    }

    fun actionSearch() {
        if (Checkers.isInternetInWifiOrData(view.mvpContext())) {
            val bitmap = view.getInputBitmap()
            if (bitmap != null) {
                view.showLoading(true)
                ImageEncoder.with(bitmap)
                        .callback { encoded ->
                            if (encoded!!.isNotEmpty()) {
                                network.searchWithPhoto(view.mvpContext(), encoded, "") { pair ->
                                    view.showLoading(false)
                                    if (pair.first!!.isEmpty()) {
                                        docs.clear()
                                        docs.addAll(pair.second!!.docs)
                                        view.showIndicatorSearchResults(docs.isEmpty())
                                        view.drawSearchResults(docs)
                                        getQuota()
                                    } else
                                        view.showErrorServer(pair.first!!)
                                }
                            } else {
                                view.showLoading(false)
                                view.showErrorImageEmpty()
                            }
                        }
                        .encode()
            } else
                view.showErrorImageEmpty()
        } else
            view.showErrorInternet()
    }

    private fun getQuota() {
        if (Checkers.isInternetInWifiOrData(view.mvpContext())) {
            view.setSearchQuota(searchQuota)
            view.setSearchPerMinute(searchPerMinute)
        }
    }
}