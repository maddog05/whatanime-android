package com.maddog05.whatanime.ui.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.maddog05.maddogutilities.callback.Callback
import com.maddog05.whatanime.BuildConfig
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.data.LogicPreferenceSharedPref
import com.maddog05.whatanime.ui.dialog.ChangelogDialog
import com.maddog05.whatanime.ui.dialog.HContentInfoDialog
import com.maddog05.whatanime.ui.tor.Navigator

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance(callback: Callback<Int>): SettingsFragment {
            val fragment = SettingsFragment()
            fragment.callback = callback
            return fragment
        }
    }

    private lateinit var callback: Callback<Int>

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_app, rootKey)
        val hContentPreference: Preference? = preferenceManager.findPreference("setting_general_enable_h_content")
        hContentPreference?.setOnPreferenceClickListener {
            if (activity != null) {
                val logicPreference = LogicPreferenceSharedPref.newInstance(activity)
                HContentInfoDialog.newInstance(activity as AppCompatActivity, object : HContentInfoDialog.OnAcceptedListener {
                    override fun isAccepted(isAccepted: Boolean) {
                        logicPreference.hContentEnabled = isAccepted
                    }
                })
                        .setIsAccepted(logicPreference.hContentEnabled)
                        .showDialog()
            }
            true
        }
        val changelogPreference: Preference? = preferenceManager.findPreference("setting_general_changelog")
        changelogPreference?.setOnPreferenceClickListener {
            ChangelogDialog.newInstance(activity as AppCompatActivity)
                    .showDialog()
            true
        }
        val reportPreference: Preference? = preferenceManager.findPreference("setting_general_report_github")
        reportPreference?.setOnPreferenceClickListener {
            Navigator.goToWebBrowser(context, getString(R.string.url_github_issues))
            true
        }
        val appDevPreference: Preference? = preferenceManager.findPreference("setting_about_developer_app")
        appDevPreference?.setOnPreferenceClickListener {
            Navigator.goToWebBrowser(context, getString(R.string.url_app_dev))
            true
        }
        val appAPIPreference: Preference? = preferenceManager.findPreference("setting_about_developer_api")
        appAPIPreference?.setOnPreferenceClickListener {
            Navigator.goToWebBrowser(context, getString(R.string.url_api_dev))
            true
        }
        val githubPreference: Preference? = preferenceManager.findPreference("setting_about_github")
        githubPreference?.setOnPreferenceClickListener {
            Navigator.goToWebBrowser(context, getString(R.string.url_github))
            true
        }
        val storePreference: Preference? = preferenceManager.findPreference("setting_about_play_store")
        storePreference?.setOnPreferenceClickListener {
            Navigator.goToWebBrowser(context, getString(R.string.url_playstore))
            true
        }
        val termsPreference: Preference? = preferenceManager.findPreference("setting_about_terms")
        termsPreference?.setOnPreferenceClickListener {
            Navigator.goToInformation(context)
            true
        }
        val appVersionPreference: Preference? = preferenceManager.findPreference("setting_about_info")
        appVersionPreference?.summary = BuildConfig.VERSION_NAME
    }
}