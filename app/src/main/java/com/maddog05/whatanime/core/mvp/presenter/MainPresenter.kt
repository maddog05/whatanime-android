package com.maddog05.whatanime.core.mvp.presenter

import com.maddog05.maddogutilities.android.Checkers
import com.maddog05.whatanime.BuildConfig
import com.maddog05.whatanime.core.data.LogicPreferenceSharedPref
import com.maddog05.whatanime.core.entity.SearchImageResult
import com.maddog05.whatanime.core.mvp.view.MainView
import com.maddog05.whatanime.core.network.LogicNetworkKotlin

class MainPresenter(private val view: MainView) {

    private val network = LogicNetworkKotlin()
    private val preferences = LogicPreferenceSharedPref.newInstance(view.mvpContext())
    private val docs = mutableListOf<SearchImageResult>()
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
            val file = view.getInputFile()
            if (file != null) {
                view.showLoading(true)
                network.searchWithPhoto(file) { pair ->
                    view.showLoading(false)
                    if (pair.first!!.isEmpty()) {
                        docs.clear()
                        docs.addAll(pair.second!!)
                        view.showIndicatorSearchResults(docs.isEmpty())
                        view.drawSearchResults(docs)
                        getQuota()
                    } else
                        view.showErrorServer(pair.first!!)
                }
            } else
                view.showErrorImageEmpty()
        } else
            view.showErrorInternet()
    }

    fun actionSearchWithUrl(url: String) {
        if (Checkers.isInternetInWifiOrData(view.mvpContext())) {
            view.showLoading(true)
            network.searchWithUrl(url) { pair ->
                view.showLoading(false)
                if (pair.first!!.isEmpty()) {
                    docs.clear()
                    docs.addAll(pair.second!!)
                    view.showIndicatorSearchResults(docs.isEmpty())
                    view.drawSearchResults(docs)
                    getQuota()
                } else
                    view.showErrorServer(pair.first!!)
            }
        } else
            view.showErrorInternet()
    }

    private fun getQuota() {
        if (Checkers.isInternetInWifiOrData(view.mvpContext())) {
            network.getQuota { pair ->
                if (pair.first!!.isEmpty()) {
                    searchPerMinute = pair.second!!.searchsPerMinute
                    view.setSearchPerMinute(searchPerMinute)
                }
            }
        } else {
            view.setSearchPerMinute(searchPerMinute)
        }
    }
}