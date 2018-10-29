package com.flayone.taxcc.taxcomparecalculate

import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.os.Build
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast


object ToastUtil {

    private var mToast: Toast? = null

    fun showToast(context: Context, msg: Int) {
        showToast(context, context.getString(msg))
    }

    fun showToast(context: Context?, msg: String?) {
        if (msg == null || "" == msg || context == null) {
            return
        }
        val duration = if (msg.length > 15) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration)
        } else {
            mToast!!.setText(msg)
            mToast!!.duration = duration
        }
        mToast!!.show()
    }

}

inline fun Activity.showAlert(func: BaseListenerDialogHelper.() -> Unit): AlertDialog =
        BaseListenerDialogHelper(this).apply { func() }.create()

inline fun Fragment.showAlert(func: BaseListenerDialogHelper.() -> Unit): AlertDialog =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BaseListenerDialogHelper(this.context!!).apply { func() }.create()
        } else {
            BaseListenerDialogHelper(this.activity).apply { func() }.create()
        }

fun showDialog(context: Activity, t: String, content: String, listener: BaseEnsureListener) {
    val dialog: AlertDialog = context.showAlert {
        title.text = t
        inputEditText.setText(content)
        okClickListener {
            if (inputEditText.text.isNullOrEmpty()) {
                ToastUtil.showToast(context, "未输入数值")
                return@okClickListener
            }
            hideDialogKeybord(dialog!!, context)
            dialog?.dismiss()
            listener.ensure(inputEditText.text.toString())
        }
        cancelClickListener {
            hideDialogKeybord(dialog!!, context)
            dialog?.dismiss()
        }
    }
    dialog.show()
}

fun hideDialogKeybord(dialog: Dialog, context: Context) {
    var b = dialog.window!!.decorView.windowToken
    if (b == null) {
        try {
            val a = context as BaseActivity
            b = a.currentFocus.windowToken
        } catch (e: Exception) {
        }
    }
    //隐藏软键盘
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(b, InputMethodManager.HIDE_NOT_ALWAYS)
}

open class MyTextWatcher(private val listener: BaseEnsureListener) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        listener.ensure(s.toString())
    }

}
