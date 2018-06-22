package com.maddog05.whatanime.core.mvp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView

interface SearchResultView {

    fun mvpIntent(): Intent

    fun mvpContext(): Context

    fun mvpStartActivity(intent: Intent)

    fun setupViews()

    fun showError(text: String)

    fun setBestResultDate(text: String)

    fun setBestResultSimilarity(text: String)

    fun setBestResultName(text: String)

    fun setRequestPhoto(bitmap: Bitmap)

    fun getRecyclerView(): RecyclerView
}