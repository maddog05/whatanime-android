package com.maddog05.whatanime.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.maddog05.whatanime.R

class QuotaInfoDialog : DialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(activity: AppCompatActivity): QuotaInfoDialog {
            val dialog = QuotaInfoDialog()
            dialog.activity = activity
            return dialog
        }
    }

    private lateinit var activity: AppCompatActivity

    private lateinit var closeBtn: AppCompatImageButton

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        try {
            val ft = manager?.beginTransaction()
            ft?.add(this, tag)
            ft?.commitAllowingStateLoss()
        } catch (ignored: IllegalStateException) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_quota_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val closeBtn: AppCompatImageButton = view.findViewById(R.id.btn_close_quota_info)
        closeBtn.setOnClickListener { dismiss() }
    }

    fun showDialog() {
        show(activity.supportFragmentManager, "quotaInfoDialog")
    }
}