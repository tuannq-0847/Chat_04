package com.sun.chat_04.ui.discovery

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.sun.chat_04.R
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.dialog_search_filter.buttonAllGenderDialog
import kotlinx.android.synthetic.main.dialog_search_filter.buttonApplyDialog
import kotlinx.android.synthetic.main.dialog_search_filter.buttonFemaleDialog
import kotlinx.android.synthetic.main.dialog_search_filter.buttonMaleDialog
import kotlinx.android.synthetic.main.dialog_search_filter.editFromAge
import kotlinx.android.synthetic.main.dialog_search_filter.editToAge
import kotlinx.android.synthetic.main.dialog_search_filter.imageCloseDialog
import kotlinx.android.synthetic.main.dialog_search_filter.seekBarDistance
import kotlinx.android.synthetic.main.dialog_search_filter.textCurrentDistance

class SearchFilterDialog : DialogFragment(), OnClickListener, OnSeekBarChangeListener {

    override fun onStart() {
        super.onStart()
        setSizeDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_search_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonAllGenderDialog -> handleButtonAllGender()
            R.id.buttonMaleDialog -> handleButtonMale()
            R.id.buttonFemaleDialog -> handleButtonFemale()
            R.id.editFromAge -> handleEditTextFromAge()
            R.id.editToAge -> handleEditTextToAge()
            R.id.buttonApplyDialog -> handleButtonApply()
            R.id.imageCloseDialog -> handleButtonExit()
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        textCurrentDistance?.text = progress.div(Constants.CONVERT_KM).toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    private fun setSizeDialog() {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        dialog?.window?.setLayout(
            (Constants.RATIO_WIDTH_DIALOG * displayMetrics.widthPixels).toInt(),
            (Constants.RATIO_HEIGHT_DIALOG * displayMetrics.heightPixels).toInt()
        )
    }

    private fun initComponents() {
        buttonAllGenderDialog.setOnClickListener(this)
        buttonMaleDialog.setOnClickListener(this)
        buttonFemaleDialog.setOnClickListener(this)
        editFromAge.setOnClickListener(this)
        editToAge.setOnClickListener(this)
        buttonApplyDialog.setOnClickListener(this)
        imageCloseDialog.setOnClickListener(this)
        seekBarDistance.setOnSeekBarChangeListener(this)
    }

    private fun handleButtonApply() {
        Intent().also {
            val bundle = Bundle()
            bundle.putInt(
                Constants.BUNDLE_FILTER_DIALOG,
                Math.round(seekBarDistance.progress.div(Constants.CONVERT_KM).toDouble()).toInt()
            )
            it.putExtras(bundle)
            targetFragment?.onActivityResult(Constants.REQUEST_CODE_DIALOG, RESULT_OK, it)
        }
    }

    private fun handleEditTextToAge() {
        editToAge?.let {
            it.isFocusableInTouchMode = true
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun handleEditTextFromAge() {
        editFromAge?.let {
            it.isFocusableInTouchMode = true
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun handleButtonFemale() {
        context?.let {
            buttonFemaleDialog?.setBackgroundResource(R.drawable.border_button_selected)
            buttonMaleDialog?.setBackgroundResource(R.drawable.border_selector)
            buttonAllGenderDialog?.setBackgroundResource(R.drawable.border_selector)
        }
    }

    private fun handleButtonMale() {
        context?.let {
            buttonFemaleDialog?.setBackgroundResource(R.drawable.border_selector)
            buttonMaleDialog?.setBackgroundResource(R.drawable.border_button_selected)
            buttonAllGenderDialog?.setBackgroundResource(R.drawable.border_selector)
        }
    }

    private fun handleButtonAllGender() {
        context?.let {
            buttonFemaleDialog?.setBackgroundResource(R.drawable.border_selector)
            buttonMaleDialog?.setBackgroundResource(R.drawable.border_selector)
            buttonAllGenderDialog?.setBackgroundResource(R.drawable.border_button_selected)
        }
    }

    private fun handleButtonExit() {
        Intent().also {
            val bundle = Bundle()
            bundle.putInt(Constants.BUNDLE_FILTER_DIALOG, Constants.CANCEL)
            it.putExtras(bundle)
            targetFragment?.onActivityResult(Constants.REQUEST_CODE_DIALOG, RESULT_OK, it)
        }
    }
}
