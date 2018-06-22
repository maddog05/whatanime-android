package com.maddog05.whatanime.ui

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.mvp.HomeView
import com.maddog05.whatanime.core.mvp.presenter.HomePresenter
import es.dmoral.toasty.Toasty

class HomeActivity : AppCompatActivity(), HomeView {

    private lateinit var presenter: HomePresenter

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var menuFab: FloatingActionButton
    private lateinit var noItemsIv: AppCompatImageView
    private lateinit var noItemsTv: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        presenter = HomePresenter(this)
        presenter.onCreate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_settings)
            presenter.openSettings()
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.processOnActivityResult(requestCode, resultCode, data)
    }

    override fun mvpContext(): Context {
        return this
    }

    override fun mvpStartActivity(intent: Intent) {
        startActivity(intent)
    }

    override fun mvpStartActivityForResult(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    override fun mvpIntent(): Intent {
        return intent!!
    }

    override fun setupViews() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        noItemsIv = findViewById(R.id.iv_home_indicator_no_items)
        noItemsTv = findViewById(R.id.tv_home_indicator_no_items)
        recyclerView = findViewById(R.id.rv_home)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        menuFab = findViewById(R.id.fab_menu)
    }

    override fun setupActions() {
        menuFab.setOnClickListener { presenter.showMenuSources() }
    }

    override fun showError(text: String) {
        Toasty.error(this, text, Toast.LENGTH_SHORT).show()
    }

    private var snackbar: Snackbar? = null

    override fun showLoading(text: String) {
        if (snackbar == null) {
            snackbar = Snackbar.make(menuFab, text, Snackbar.LENGTH_INDEFINITE)
            snackbar!!.show()
        }
    }

    override fun hideLoading() {
        if (snackbar != null) {
            snackbar!!.dismiss()
        }
    }

    override fun showNoItems(wantVisible: Boolean) {
        val visibility = if (wantVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
        noItemsIv.visibility = visibility
        noItemsTv.visibility = visibility
    }

    override fun getRecyclerView(): RecyclerView {
        return recyclerView
    }
}
