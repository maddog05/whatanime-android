package com.maddog05.whatanime.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.maddog05.whatanime.R
import com.maddog05.whatanime.ui.adapter.AdapterChangelog
import com.maddog05.whatanime.util.Mapper
import java.io.InputStream
import java.nio.charset.Charset

/*
 * Created by andreetorres on 26/01/18.
 */
class ChangelogDialog : DialogFragment() {

    private lateinit var activity: AppCompatActivity

    private lateinit var closeBtn: AppCompatImageButton
    private lateinit var titleTv: AppCompatTextView
    private lateinit var descriptionTv: AppCompatTextView
    private lateinit var changesRv: RecyclerView

    companion object {
        @JvmStatic
        fun newInstance(activity: AppCompatActivity): ChangelogDialog {
            val dialog = ChangelogDialog()
            dialog.activity = activity
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
        val root = inflater.inflate(R.layout.dialog_changelog, container, false)
        titleTv = root.findViewById(R.id.tv_title_changelog)
        descriptionTv = root.findViewById(R.id.tv_description_changelog)
        changesRv = root.findViewById(R.id.rv_changes)
        changesRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        closeBtn = root.findViewById(R.id.btn_close_changelog)
        closeBtn.setOnClickListener { dismiss() }

        setupData()

        return root
    }

    private fun setupData() {
        if (context != null) {
            try {
                val inputStream: InputStream = context!!.assets.open("changelog.json")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                val stringJson = String(buffer, Charset.forName("UTF-8"))

                val jsonChangelog = JsonParser().parse(stringJson).asJsonObject
                titleTv.text = jsonChangelog.get("title").asString
                descriptionTv.text = jsonChangelog.get("description").asString
                val jsonVersions: JsonArray = jsonChangelog.get("versions").asJsonArray
                val versions = Mapper.parseChangelog(jsonVersions)
                val adapter = AdapterChangelog(versions)
                changesRv.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                dismiss()
            }
        }
    }

    fun showDialog() {
        show(activity.supportFragmentManager, "changelogDialog")
    }
}