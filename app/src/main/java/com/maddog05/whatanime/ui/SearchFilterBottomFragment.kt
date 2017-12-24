package com.maddog05.whatanime.ui

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import com.maddog05.maddogutilities.callback.Callback

import com.maddog05.whatanime.R
import com.maddog05.whatanime.util.C
import es.dmoral.toasty.Toasty

class SearchFilterBottomFragment : BottomSheetDialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(previousFilter: String, callback: Callback<String>): SearchFilterBottomFragment {
            val instance = SearchFilterBottomFragment()
            instance.previousFilter = previousFilter
            instance.callback = callback
            return instance
        }
    }

    private lateinit var callback: Callback<String>
    private lateinit var previousFilter: String

    private lateinit var applyBtn: AppCompatButton
    private lateinit var optionsRg: RadioGroup
    private lateinit var yearEt: AppCompatEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_search_filter_bottom, container, false)
        setupViews(root)
        setupActions()
        setupData()
        return root
    }

    private fun setupViews(root: View) {
        applyBtn = root.findViewById(R.id.btn_close_filter)
        optionsRg = root.findViewById(R.id.rg_filter)
        yearEt = root.findViewById(R.id.et_filter_year)
    }

    private fun setupActions() {
        optionsRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_filter_no -> {
                    yearEt.isEnabled = false
                }
                R.id.rb_filter_by_year -> {
                    yearEt.isEnabled = true
                }
            }
        }
        applyBtn.setOnClickListener {
            when (optionsRg.checkedRadioButtonId) {
                R.id.rb_filter_no -> {
                    callback.done(C.FILTER_DEFAULT)
                    showSuccess(getString(R.string.success_filter_applied))
                }
                R.id.rb_filter_by_year -> {
                    if (!yearEt.text.toString().trim().isEmpty()) {
                        val year = yearEt.text.toString().toInt()
                        if (year in 1970..C.currentYear()) {
                            val yearS = yearEt.text.toString() + "-" + C.FILTER_DEFAULT
                            callback.done(yearS)
                            showSuccess(getString(R.string.success_filter_applied))
                        } else {
                            showError(getString(R.string.error_range_year))
                        }
                    } else {
                        showError(getString(R.string.error_range_year))
                    }
                }
            }
            dismiss()
        }
    }

    private fun setupData() {
        if (previousFilter == C.FILTER_DEFAULT) {
            optionsRg.check(R.id.rb_filter_no)
        } else {
            optionsRg.check(R.id.rb_filter_by_year)
            val index = previousFilter.indexOf("-")
            if (index > -1)
                yearEt.setText(previousFilter.substring(0, index))
        }
    }

    private fun showSuccess(text: String) {
        if (context != null)
            Toasty.success(context!!, text, Toast.LENGTH_SHORT).show()
    }

    private fun showError(text: String) {
        if (context != null)
            Toasty.error(context!!, text, Toast.LENGTH_SHORT).show()
    }
}
