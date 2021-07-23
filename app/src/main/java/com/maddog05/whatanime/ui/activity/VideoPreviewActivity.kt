package com.maddog05.whatanime.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devbrackets.android.exomedia.listener.OnCompletionListener
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import com.maddog05.maddogutilities.android.Checkers
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.entity.output.SearchDetail
import com.maddog05.whatanime.databinding.ActivityVideoPreviewBinding
import com.maddog05.whatanime.util.C
import com.maddog05.whatanime.util.Mapper
import es.dmoral.toasty.Toasty

class VideoPreviewActivity : AppCompatActivity(R.layout.activity_video_preview), OnPreparedListener,
    OnCompletionListener {
    private var videoUrl = C.EMPTY
    private var doc: SearchDetail.Doc? = null

    private lateinit var binding: ActivityVideoPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupExtraData()

        setSupportActionBar(binding.toolbarVideoPreview)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.videoViewPreview.setOnPreparedListener(this)
        binding.videoViewPreview.setOnCompletionListener(this)
        binding.btnShareVideoPreview.setOnClickListener { actionShare() }
        binding.btnExpandVideoPreview.setOnClickListener { actionExpand() }
        setupData()
    }

    override fun onPause() {
        super.onPause()
        if (binding.videoViewPreview.isPlaying) {
            binding.videoViewPreview.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.pbarLoadingVideoPreview.visibility == View.GONE && !binding.videoViewPreview.isPlaying) binding.videoViewPreview.start()
    }

    private fun setupExtraData() {
        val bundle = intent.extras
        if (bundle != null) {
            videoUrl = bundle.getString(C.Extras.VIDEO_URL, C.EMPTY)
            doc = bundle.getParcelable(C.Extras.DOC)
        }
    }

    private fun setupData() {
        val title =
            if (doc!!.romanjiTitle != null && doc!!.romanjiTitle.isNotEmpty()) doc!!.romanjiTitle else doc!!.anime
        setupTitle(title)
        if (Checkers.isInternetInWifiOrData(this@VideoPreviewActivity)) {
            binding.videoViewPreview.setVideoURI(Uri.parse(videoUrl))
        } else {
            showError(getString(R.string.error_internet_connection))
        }
    }

    private fun showError(text: String) {
        Toasty.error(this@VideoPreviewActivity, text, Toast.LENGTH_SHORT).show()
    }

    private fun setupTitle(text: String) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = text
        }
    }

    private fun actionExpand() {
        window.decorView.let {
            val isNotFullScreen = it.systemUiVisibility == View.SYSTEM_UI_FLAG_VISIBLE
            if (isNotFullScreen) {
                goFullscreen()
            } else {
                exitFullscreen()
            }
        }
    }

    private fun actionShare() {
        val nameAndEpisodeText = Mapper.parseEpisodeNumber(this, doc!!.episode)
        val title =
            if (doc!!.romanjiTitle != null && doc!!.romanjiTitle.isNotEmpty()) doc!!.romanjiTitle else doc!!.anime
        val text = (title
                + C.SPACE
                + nameAndEpisodeText
                + C.SPACE
                + getString(R.string.share_founded_with)
                + C.SPACE
                + getString(R.string.app_name))
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, getString(R.string.action_share)))
    }

    public override fun onDestroy() {
        super.onDestroy()
        exitFullscreen()
    }

    private fun goFullscreen() {
        setUiFlags(true)
    }

    private fun exitFullscreen() {
        setUiFlags(false)
    }

    private fun setUiFlags(fullscreen: Boolean) {
        window.decorView.let {
            it.systemUiVisibility =
                if (fullscreen) getFullscreenUiFlags() else View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    private fun getFullscreenUiFlags(): Int {
        var flags = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        flags = flags or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        return flags
    }

    override fun onPrepared() {
        binding.pbarLoadingVideoPreview.visibility = View.GONE
        binding.videoViewPreview.start()
    }

    override fun onCompletion() {
        binding.pbarLoadingVideoPreview.visibility = View.GONE
        binding.videoViewPreview.restart()
    }

    //BACK
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}