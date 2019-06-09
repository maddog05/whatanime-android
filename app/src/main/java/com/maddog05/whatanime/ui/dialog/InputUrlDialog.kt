package com.maddog05.whatanime.ui.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import com.maddog05.maddogutilities.callback.Callback
import com.maddog05.whatanime.R

/*
 * Created by andreetorres on 25/01/18.
 */
class InputUrlDialog(val context: Context) {
    private lateinit var callback: Callback<String>

    fun setCallback(callback: Callback<String>) {
        this.callback = callback
    }

    fun show() {
        val editText = AppCompatEditText(context)
        editText.setSingleLine(true)
        editText.imeOptions = EditorInfo.IME_ACTION_DONE
        val container = FrameLayout(context)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin = context.resources.getDimensionPixelSize(R.dimen.activity_margin)
        params.rightMargin = params.leftMargin
        editText.layoutParams = params
        container.addView(editText)

        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.input_source_url)
                .setView(container)
                .setCancelable(false)
                .setPositiveButton(R.string.action_ok) { dialog, _ ->
                    dialog.dismiss()
                    callback.done(editText.text.toString())
                }
                .setNegativeButton(R.string.action_close) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }
}