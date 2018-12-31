package com.maddog05.whatanime.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatImageButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.maddog05.whatanime.R

class HContentInfoDialog : DialogFragment() {
    companion object {
        @JvmStatic
        fun newInstance(activity: AppCompatActivity, listener: OnAcceptedListener): HContentInfoDialog {
            val dialog = HContentInfoDialog()
            dialog.activity = activity
            dialog.listener = listener
            return dialog
        }
    }

    private lateinit var listener: OnAcceptedListener
    private lateinit var activity: AppCompatActivity
    private lateinit var acceptChk: AppCompatCheckBox
    private lateinit var acceptBtn: AppCompatButton
    private var isAccepted = false

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
        return inflater.inflate(R.layout.dialog_h_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val closeBtn: AppCompatImageButton = view.findViewById(R.id.btn_close_h_content)
        closeBtn.setOnClickListener { dismiss() }
        acceptChk = view.findViewById(R.id.chk_h_content)
        acceptBtn = view.findViewById(R.id.btn_accept_h_content)
        acceptBtn.isEnabled = isAccepted
        acceptChk.isChecked = isAccepted
        acceptChk.setOnCheckedChangeListener { _, isChecked ->
            this.isAccepted = isChecked
        }
        acceptBtn.setOnClickListener { listener.isAccepted(this.isAccepted) }
    }

    fun setIsAccepted(isAccepted: Boolean): HContentInfoDialog {
        this.isAccepted = isAccepted
        return this
    }

    fun showDialog() {
        show(activity.supportFragmentManager, "quotaInfoDialog")
    }

    interface OnAcceptedListener {
        fun isAccepted(isAccepted: Boolean)
    }
}