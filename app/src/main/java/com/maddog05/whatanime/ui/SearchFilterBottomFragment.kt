package com.maddog05.whatanime.ui

import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.FragmentManager
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.view.View
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

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val root = View.inflate(context, R.layout.fragment_search_filter_bottom, null)
        dialog.setContentView(root)
        setupViews(root)
        setupActions()
        setupData()
        val params = (root.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior) {
            behavior.setBottomSheetCallback(bottomSheetCallback)
        }
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        try {
            val ft = manager?.beginTransaction()
            ft?.add(this, tag)
            ft?.commitAllowingStateLoss()
        } catch (ignored: IllegalStateException) {

        }
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
                        val year = if (yearEt.text.isEmpty()) {
                            0
                        } else {
                            yearEt.text.toString().toInt()
                        }
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
