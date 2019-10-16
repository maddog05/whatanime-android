package com.maddog05.whatanime.ui.dialog

import android.app.Dialog
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.entity.output.SearchDetail
import com.maddog05.whatanime.core.image.GlideLoader
import com.maddog05.whatanime.util.Mapper

class SearchResultInfoDialog : BottomSheetDialogFragment() {

    interface OnSearchResultOptionListener {
        fun OnShareText()
        fun OnShowSample()
    }

    var listener: OnSearchResultOptionListener? = null
    lateinit var doc: SearchDetail.Doc

    private lateinit var photoIv: AppCompatImageView
    private lateinit var closeBtn: AppCompatImageButton
    private lateinit var shareBtn: AppCompatImageButton
    private lateinit var watchBtn: MaterialButton
    private lateinit var titleTv: AppCompatTextView
    private lateinit var episodeTv: AppCompatTextView

    fun withDoc(doc: SearchDetail.Doc): SearchResultInfoDialog {
        this.doc = doc
        return this
    }

    fun withListener(listener: OnSearchResultOptionListener?): SearchResultInfoDialog {
        this.listener = listener
        return this
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = View.inflate(context, R.layout.fragment_search_result_info, null)
        dialog.setContentView(view)

        photoIv = view.findViewById(R.id.iv_search_result_info_photo)
        closeBtn = view.findViewById(R.id.btn_search_result_info_close)
        closeBtn.setOnClickListener { dismiss() }
        watchBtn = view.findViewById(R.id.btn_search_result_info_video)
        watchBtn.setOnClickListener {
            listener?.OnShowSample()
            dismiss()
        }
        shareBtn = view.findViewById(R.id.btn_search_result_info_share)
        shareBtn.setOnClickListener {
            listener?.OnShareText()
            dismiss()
        }
        titleTv = view.findViewById(R.id.tv_search_result_info_title)
        titleTv.text = if (doc.romanjiTitle != null && doc.romanjiTitle.isNotEmpty()) doc.romanjiTitle else doc.anime
        episodeTv = view.findViewById(R.id.tv_search_result_info_episode)
        episodeTv.text = Mapper.parseEpisodeNumber(context!!, doc.episode)

        val imageLoader: GlideLoader = GlideLoader.create()
        imageLoader.with(context)
                .placeholder(R.drawable.ic_photo)
                .load(Mapper.getImageUrl(doc))
                .target(photoIv)
                .callback { isCompleted ->
                    if (isCompleted)
                        photoIv.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                .start()

        val parentLayoutParams = (view.parent as View).layoutParams
        if (parentLayoutParams != null && parentLayoutParams is CoordinatorLayout.LayoutParams) {
            val behavior = parentLayoutParams.behavior
            if (behavior != null && behavior is BottomSheetBehavior) {
                behavior.setBottomSheetCallback(bottomSheetCallback)
            }
        }
    }
}