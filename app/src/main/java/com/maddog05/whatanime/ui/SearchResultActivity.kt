package com.maddog05.whatanime.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.maddog05.maddogutilities.view.SquareImageView
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.mvp.SearchResultView
import com.maddog05.whatanime.core.mvp.presenter.SearchResultPresenter
import es.dmoral.toasty.Toasty

class SearchResultActivity : AppCompatActivity(), SearchResultView {

    private lateinit var presenter: SearchResultPresenter

    private lateinit var toolbar: Toolbar
    private lateinit var photoIv: SquareImageView
    private lateinit var similarityTv: AppCompatTextView
    private lateinit var nameTv: AppCompatTextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        presenter = SearchResultPresenter(this)
        presenter.onCreate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_result, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        else if (item.itemId == R.id.action_share)
            presenter.shareSearchResult()
        return super.onOptionsItemSelected(item)
    }

    override fun mvpIntent(): Intent {
        return intent
    }

    override fun mvpContext(): Context {
        return this
    }

    override fun mvpStartActivity(intent: Intent) {
        startActivity(intent)
    }

    override fun setupViews() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true)
        photoIv = findViewById(R.id.iv_photo)
        similarityTv = findViewById(R.id.tv_search_result_similarity)
        nameTv = findViewById(R.id.tv_search_result_name)
        recyclerView = findViewById(R.id.rv_results)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //recyclerView.isNestedScrollingEnabled=false
    }

    override fun showError(text: String) {
        Toasty.error(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun setBestResultDate(text: String) {
        toolbar.title = text
    }

    override fun setBestResultSimilarity(text: String) {
        similarityTv.text = text
    }

    override fun setBestResultName(text: String) {
        nameTv.text = text
    }

    override fun setRequestPhoto(bitmap: Bitmap) {
        photoIv.setImageBitmap(bitmap)
    }

    override fun getRecyclerView(): RecyclerView {
        return recyclerView
    }
}