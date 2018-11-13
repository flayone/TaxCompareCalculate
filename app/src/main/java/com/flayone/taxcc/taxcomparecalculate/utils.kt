package com.flayone.taxcc.taxcomparecalculate

import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.os.Build
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable


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

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

open class BaseModel : Serializable, JSONConvertable
class CalculateHistoryModel : BaseModel() {
    var baseSalary = "0" //税前工资
    var welfare = "0"  //五险一金
    var expend = "0"  //附加扣除数
    var afterTex = "0"  //税后
    var tax = "0"  //个税
    var taxThreshold = "0"  //自定义起征数

    override fun equals(other: Any?): Boolean {
        if (this === other) return true   //若指向同一个对象，直接返回true
        val flag = other is CalculateHistoryModel    //判断obj是否属于User这个类
        return if (flag) {
            val model = other as CalculateHistoryModel
            //如果输入参数都相同代表是同一条数据
            model.expend == this.expend && model.welfare == this.welfare && model.baseSalary == this.baseSalary
        } else {
            false
        }
    }
}

class HistoryListModel : BaseModel() {
    var list = mutableListOf<CalculateHistoryModel>()
}

interface JSONConvertable {
    fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T : JSONConvertable> String.toObject(): T = Gson().fromJson(this, T::class.java)

inline fun <reified T : JSONConvertable> String.toList(): MutableList<T> = Gson().fromJson(this, object : TypeToken<List<T>>() {}.type)


val HISTORY_TAG = "historyList"
val HISTORY_TAG_A = "historyListA"
val LOCAL_Data = "LocalData"