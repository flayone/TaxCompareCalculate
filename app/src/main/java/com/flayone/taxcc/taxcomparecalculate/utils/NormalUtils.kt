package com.flayone.taxcc.taxcomparecalculate.utils

import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import com.flayone.taxcc.taxcomparecalculate.dialog.BaseListenerDialogHelper


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


//基础的recycleView的adapter封装
abstract class BaseRecycleListAdapter(open var list: List<Any>, @LayoutRes private val resourceId: Int) : RecyclerView.Adapter<MyViewHolder>() {
    lateinit var mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        mContext = parent.context
        return MyViewHolder(View.inflate(parent.context, resourceId, null))
    }

    override fun getItemCount(): Int = list.size

    // 如果想要每次都调用onBindViewHolder()刷新item数据，就要重写getItemViewType()，让其返回position，否则很容易产生数据错乱的现象。
    override fun getItemViewType(position: Int): Int = position

    fun toast(S:String){
        ToastUtil.showToast(mContext,"toast=$S")
    }
}

fun initRecycleLayoutManger(context: Context): LinearLayoutManager {
    val layoutManager = object : LinearLayoutManager(context) {
        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
                RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    layoutManager.orientation = LinearLayoutManager.VERTICAL
    return layoutManager
}



//(以上海为例)公积金下限基数 2190 社保下限基数 4279
private const val minSocialSafety = 4279
private const val maxSocialSafety = 21396
private const val socialSafetyPercent = "0.105"

private const val minPublicMoney = 2190
private const val maxPublicMoney = 19512
private const val publicMoneyPercent = "0.07"

private const val missCount = "0"//修正参数

//(以上海为例)计算险金扣除数
fun calculateWelfare(salaryVal: String): String {
    return when (salaryVal.toDouble()) {
        in 0..minPublicMoney -> //公积金下限、社保下限以下。
            add(multiply(minPublicMoney.toString(), publicMoneyPercent, 2), multiply(minSocialSafety.toString(), socialSafetyPercent, 2), missCount)
        in minPublicMoney..minSocialSafety -> //公积金7%、社保下限
            add(multiply(salaryVal, publicMoneyPercent, 2), multiply(minSocialSafety.toString(), socialSafetyPercent, 2), missCount)
        in minSocialSafety..maxPublicMoney -> //公积金7%,社保10.5%
            add(multiply(salaryVal, publicMoneyPercent, 2), multiply(salaryVal, socialSafetyPercent, 2), missCount)
        in maxPublicMoney..maxSocialSafety -> //公积金上限,社保10.5%
            add(multiply(maxPublicMoney.toString(), publicMoneyPercent, 2), multiply(salaryVal, socialSafetyPercent, 2), missCount)
        in maxSocialSafety..Int.MAX_VALUE -> {//公积金上限,社保上限
            add(multiply(maxPublicMoney.toString(), publicMoneyPercent, 2), multiply(maxSocialSafety.toString(), socialSafetyPercent, 2), missCount)
        }
        else -> "0"
    }
}

/**
 * 根据 levelList 和 taxRateList 来计算速算扣除数
 * levelList 月应纳税所得额（元）的集合
 * taxRateList 税率的集合
 */
fun getQuickDeductionList(levelList: MutableList<Int>, taxRateList: MutableList<String>): MutableList<Int> {
    val quickDeductionList = mutableListOf<Int>()//速算扣除数的集合
    //速算扣除数的计算公式是： 本级速算扣除额=上一级最高应纳税所得额×（本级税率-上一级税率）+上一级速算扣除数
    for (i in 1..levelList.size) {
        if (i == 1) {
            quickDeductionList.add(0)
        } else {
            quickDeductionList.add((levelList[i - 1] * (subtract(taxRateList[i - 1], taxRateList[i - 2]).toDouble())).toInt() + quickDeductionList[i - 2])
        }
    }
    return quickDeductionList
}


