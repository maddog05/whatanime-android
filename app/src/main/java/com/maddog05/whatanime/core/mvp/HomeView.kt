package com.maddog05.whatanime.core.mvp

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView

interface HomeView {
    fun mvpContext(): Context

    fun mvpStartActivity(intent: Intent)

    fun mvpStartActivityForResult(intent: Intent, requestCode: Int)

    fun mvpIntent(): Intent

    fun setupViews()

    fun setupActions()

    fun showError(text: String)

    fun showLoading(text: String)

    fun hideLoading()

    fun showNoItems(wantVisible: Boolean)

    fun getRecyclerView(): RecyclerView
}