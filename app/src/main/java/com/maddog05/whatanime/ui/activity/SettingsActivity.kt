package com.maddog05.whatanime.ui.activity

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.maddog05.whatanime.R
import com.maddog05.whatanime.databinding.ActivitySettingsBinding
import com.maddog05.whatanime.ui.fragment.SettingsFragment
import com.maddog05.whatanime.util.C

class SettingsActivity : AppCompatActivity(R.layout.activity_settings) {

    private lateinit var binding: ActivitySettingsBinding
    private val modifiedSettings = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_settings,
            SettingsFragment.newInstance { modifiedValue ->
                modifiedSettings.add(modifiedValue)
            }
        )
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val bundle = Bundle()
        setResult(
            if (modifiedSettings.isNotEmpty()) {
                for (i in 0 until modifiedSettings.size) {
                    if (modifiedSettings[i] == C.SETTING_MODIFIED_CLEAR_DATABASE)
                        bundle.putBoolean(C.Extras.SETTING_CHANGE_CLEAR_HISTORY, true)
                }
                Activity.RESULT_OK
            } else {
                Activity.RESULT_CANCELED
            }
        )
        intent!!.putExtras(bundle)
        super.onBackPressed()
    }
}