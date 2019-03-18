package com.flayone.taxcc.taxcomparecalculate.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.view.View
import com.flayone.taxcc.taxcomparecalculate.utils.hideDialogKeybord

abstract class BaseDialogHelper(private val context: Context) {

    abstract val dialogView: View
    abstract val builder: AlertDialog.Builder

    //  required bools
    open var cancelable: Boolean = true
    open var isBackGroundTransparent: Boolean = false

    //  dialog
    open var dialog: AlertDialog? = null

    //  dialog create
    open fun create(): AlertDialog {
        dialog = builder
                .setCancelable(cancelable)
                .create()

        //  very much needed for customised dialogs
        if (isBackGroundTransparent)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setOnDismissListener {
            //dialog消失后 windowToken会变为空，这时候改用activity的windowToken来隐藏软键盘
            hideDialogKeybord(dialog!!,context)
        }
//        dialog?
        return dialog!!
    }

    //  cancel listener
    open fun onCancelListener(func: () -> Unit): AlertDialog.Builder? =
            builder.setOnCancelListener {
                func()
            }
}