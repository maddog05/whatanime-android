package com.maddog05.whatanime.core.mvp.view

import android.content.Context
import android.graphics.Bitmap
import com.maddog05.whatanime.core.entity.output.SearchDetail

interface MainView {
    fun mvpContext(): Context
    fun drawSearchResults(results: MutableList<SearchDetail.Doc>)
    fun getInputBitmap(): Bitmap?
    fun setSearchPerMinute(number: Int)
    fun showChangelog()
    fun showErrorImageEmpty()
    fun showErrorInternet()
    fun showErrorServer(text: String)
    fun showIndicatorSearchResults(wantVisible: Boolean)
    fun showLoading(wantVisible: Boolean)
}