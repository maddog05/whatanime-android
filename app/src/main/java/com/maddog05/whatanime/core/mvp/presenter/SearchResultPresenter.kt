package com.maddog05.whatanime.core.mvp.presenter

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import com.maddog05.maddogutilities.callback.Callback
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.Logic
import com.maddog05.whatanime.core.LogicApp
import com.maddog05.whatanime.core.entity.ResponseEntity
import com.maddog05.whatanime.core.entity.SearchDetail
import com.maddog05.whatanime.core.mvp.SearchResultView
import com.maddog05.whatanime.ui.adapter.AdapterSearchResultHome
import com.maddog05.whatanime.ui.tor.Navigator
import com.maddog05.whatanime.util.C
import com.maddog05.whatanime.util.Mapper

class SearchResultPresenter(val view: SearchResultView) {

    private var logic: LogicApp = Logic.get(view.mvpContext())
    private lateinit var adapter: AdapterSearchResultHome

    private var requestId: Long = 0
    private var textToShare: String = ""
    private var imageUrl = ""

    fun onCreate() {
        view.setupViews()
        adapter = AdapterSearchResultHome(view.mvpContext())
        adapter.setClickCallback(Callback { response -> processClickResponse(response) })
        adapter.setLongClickCallback(Callback { response->processLongClickResponse(response) })
        val bundle = view.mvpIntent().extras
        requestId = bundle.getLong(C.Extras.ID_REQUEST)
        val request = logic.databaseGetRequest(requestId)
        adapter.setupData(request.responses)
        view.getRecyclerView().adapter = adapter
        view.setBestResultDate(Mapper.parseTimelineDate(request.date)
                + " "
                + Mapper.parseTimelineHour(view.mvpContext(), request.date))
        if (request.responses.isNotEmpty()) {
            val response = request.responses[0]
            view.setBestResultSimilarity(Mapper.parsePercentageSimilarity(response.similarity))
            val nameAndEpisodeText = Mapper.parseEpisodeNumber(view.mvpContext(), response.episode)
            view.setBestResultName(response.name
                    + C.SPACE
                    + nameAndEpisodeText)
            textToShare = response.name +
                    C.SPACE + nameAndEpisodeText +
                    C.SPACE + view.mvpContext().getString(R.string.share_founded_with) +
                    C.SPACE + view.mvpContext().getString(R.string.app_name)
        }
        imageUrl = request.imageUrl
        logic.loadImageUrl(imageUrl, { bitmap -> view.setRequestPhoto(bitmap) })
    }

    private fun processLongClickResponse(response: ResponseEntity) {

    }

    private fun processClickResponse(response: ResponseEntity) {
        val url = Mapper.getVideoUrl(response)
        val doc: SearchDetail.Doc = SearchDetail.Doc()
        doc.anime = response.anime
        doc.episode = response.episode
        doc.romanjiTitle = response.name
        Navigator.goToPreviewVideo(view.mvpContext() as AppCompatActivity, url, doc)
    }

    fun shareSearchResult() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_TEXT, textToShare)
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUrl))
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        view.mvpStartActivity(Intent.createChooser(intent, view.mvpContext().getString(R.string.action_share)))
    }
}