package com.maddog05.whatanime.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.entity.SearchImageResult
import com.maddog05.whatanime.core.image.GlideLoader
import com.maddog05.whatanime.databinding.FragmentSearchResultInfoBinding

class SearchResultInfoDialog : DialogFragment() {

    interface OnSearchResultOptionListener {
        fun onShareText()
        fun onShowSample()
    }

    private var _binding: FragmentSearchResultInfoBinding? = null
    private val binding get() = _binding!!

    var listener: OnSearchResultOptionListener? = null
    lateinit var doc: SearchImageResult

    fun withDoc(doc: SearchImageResult): SearchResultInfoDialog {
        this.doc = doc
        return this
    }

    fun withListener(listener: OnSearchResultOptionListener?): SearchResultInfoDialog {
        this.listener = listener
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentSearchResultInfoBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog= AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.btnSearchResultInfoClose.setOnClickListener { dismiss() }
        binding.btnSearchResultInfoVideo.setOnClickListener {
            listener?.onShowSample()
            dismiss()
        }
        binding.btnSearchResultInfoShare.setOnClickListener {
            listener?.onShareText()
            dismiss()
        }
        binding.tvSearchResultInfoTitle.text = doc.filename

        val imageLoader: GlideLoader = GlideLoader.create()
        imageLoader.with(context)
            .placeholder(R.drawable.ic_photo)
            .load(doc.imageUrl)
            .target(binding.ivSearchResultInfoPhoto)
            .callback { isCompleted ->
                if (isCompleted)
                    binding.ivSearchResultInfoPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            .start()
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}