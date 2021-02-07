package com.maddog05.whatanime.core.mvp.presenter

import com.maddog05.maddogutilities.android.Checkers
import com.maddog05.whatanime.BuildConfig
import com.maddog05.whatanime.core.data.LogicPreferenceSharedPref
import com.maddog05.whatanime.core.entity.output.SearchDetail
import com.maddog05.whatanime.core.image.KImageUtil
import com.maddog05.whatanime.core.mvp.view.MainView
import com.maddog05.whatanime.core.network.LogicNetworkRetrofit

class MainPresenter(private val view: MainView) {

    private val network = LogicNetworkRetrofit.newInstance()
    private val preferences = LogicPreferenceSharedPref.newInstance(view.mvpContext())
    private val docs = mutableListOf<SearchDetail.Doc>()
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
                KImageUtil.ImageEncoderAsyncTask(object : KImageUtil.OnEncodedListener {
                    override fun onComplete(encoded: String) {
                        if (encoded!!.isNotEmpty()) {
                            network.searchWithPhoto(view.mvpContext(), encoded, "") { pair ->
                                view.showLoading(false)
                                if (pair.first!!.isEmpty()) {
                                    docs.clear()
                                    docs.addAll(filterHContent(pair.second!!.docs))
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
                }).execute(bitmap)
            } else
                view.showErrorImageEmpty()
        } else
            view.showErrorInternet()
    }

    private fun filterHContent(items: MutableList<SearchDetail.Doc>): MutableList<SearchDetail.Doc> {
        val isHContentEnabled = preferences.hContentEnabled
        if (isHContentEnabled)
            return items
        else {
            val response = mutableListOf<SearchDetail.Doc>()
            for (i in items.indices) {
                val doc = items[i]
                if (!doc.isHentai)
                    response.add(doc)
            }
            return response
        }
    }

    private fun getQuota() {
        if (Checkers.isInternetInWifiOrData(view.mvpContext())) {
            network.getQuota(view.mvpContext()) { pair ->
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