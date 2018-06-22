package com.maddog05.whatanime.ui.fragment

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.maddog05.maddogutilities.callback.Callback
import com.maddog05.whatanime.BuildConfig
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.LogicApp
import com.maddog05.whatanime.ui.dialog.ChangelogDialog
import com.maddog05.whatanime.ui.tor.Navigator
import com.maddog05.whatanime.util.C
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance(logicApp: LogicApp, callback: Callback<Int>): SettingsFragment {
            val fragment = SettingsFragment()
            fragment.logic = logicApp
            fragment.callback = callback
            return fragment
        }
    }

    private lateinit var logic: LogicApp
    private lateinit var callback: Callback<Int>

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_app, rootKey)
        val clearSearchPreference = preferenceManager.findPreference("setting_general_clear_history")
        clearSearchPreference.setOnPreferenceClickListener {
            //ASK TO DELETE DATABASE
            AlertDialog.Builder(context!!)
                    .setMessage(R.string.question_clear_history)
                    .setPositiveButton(R.string.action_ok, { dialog, _ ->
                        logic.databaseClearRequests()
                        callback.done(C.SETTING_MODIFIED_CLEAR_DATABASE)
                        dialog.dismiss()
                    })
                    .setNegativeButton(R.string.action_close, { dialog, _ ->
                        dialog.dismiss() })
                    .show()

            true
        }
        val changelogPreference = preferenceManager.findPreference("setting_general_changelog")
        changelogPreference.setOnPreferenceClickListener {
            ChangelogDialog.newInstance(activity as AppCompatActivity)
                    .showDialog()
            true
        }
        val appDevPreference = preferenceManager.findPreference("setting_about_developer_app")
        appDevPreference.setOnPreferenceClickListener {
            Navigator.goToWebBrowser(context, getString(R.string.url_app_dev))
            true
        }
        val appAPIPreference = preferenceManager.findPreference("setting_about_developer_api")
        appAPIPreference.setOnPreferenceClickListener {
            Navigator.goToWebBrowser(context, getString(R.string.url_api_dev))
            true
        }
        val githubPreference = preferenceManager.findPreference("setting_about_github")
        githubPreference.setOnPreferenceClickListener {
            Navigator.goToWebBrowser(context, getString(R.string.url_github))
            true
        }
        val storePreference = preferenceManager.findPreference("setting_about_play_store")
        storePreference.setOnPreferenceClickListener {
            Navigator.goToWebBrowser(context, getString(R.string.url_playstore))
            true
        }
        val termsPreference = preferenceManager.findPreference("setting_about_terms")
        termsPreference.setOnPreferenceClickListener {
            Navigator.goToInformation(context)
            true
        }
        val appVersionPreference = preferenceManager.findPreference("setting_about_info")
        appVersionPreference.setSummary(BuildConfig.VERSION_NAME)
    }
}