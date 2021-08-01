package com.maddog05.whatanime.core.mvp.view

import android.content.Context
import com.maddog05.whatanime.core.entity.SearchImageResult
import java.io.File

interface MainView {
    fun mvpContext(): Context
    fun drawSearchResults(results: MutableList<SearchImageResult>)
    fun getInputFile(): File?
    fun setSearchPerMinute(number: Int)
    fun showChangelog()
    fun showErrorImageEmpty()
    fun showErrorInternet()
    fun showErrorServer(text: String)
    fun showIndicatorSearchResults(wantVisible: Boolean)
    fun showLoading(wantVisible: Boolean)
}