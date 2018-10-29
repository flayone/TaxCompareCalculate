package com.flayone.taxcc.taxcomparecalculate

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.input_dialog.view.*

class BaseListenerDialogHelper(context: Context) : BaseDialogHelper(context) {
    override val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.input_dialog, null)

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    val okBtn = dialogView.ok_btn!!
    val inputEditText = dialogView.et_threshold!!
    val title = dialogView.dialog_title!!
    val cancelBtn = dialogView.cancel_btn!!

    fun okClickListener(func: (() -> Unit)? = null) = with(okBtn) {
        setClickListener(func)
    }

    fun cancelClickListener(func: (() -> Unit)? = null) = with(cancelBtn) {
        setClickListener(func)
    }

    private fun View.setClickListener(func: (() -> Unit)?) = setOnClickListener {
        func?.invoke()
//        dialog?.dismiss()
    }

}