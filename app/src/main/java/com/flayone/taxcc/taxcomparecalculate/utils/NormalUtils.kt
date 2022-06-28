package com.flayone.taxcc.taxcomparecalculate.utils

import android.animation.Animator
import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.flayone.taxcc.taxcomparecalculate.WebViewActivity
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import com.flayone.taxcc.taxcomparecalculate.base.BaseApp
import com.flayone.taxcc.taxcomparecalculate.dialog.BaseListenerDialogHelper
import org.jetbrains.anko.windowManager


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
            b = a.currentFocus?.windowToken
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

    fun toast(S: String) {
        ToastUtil.showToast(mContext, "toast=$S")
    }
}

fun initRecycleLayoutManger(context: Context): LinearLayoutManager {
    val layoutManager = object : LinearLayoutManager(context) {
        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
                RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    layoutManager.orientation = RecyclerView.VERTICAL
    return layoutManager
}


//(以上海为例)2018年度公积金下限基数 2190 ,2018年度社保下限基数 4279
//本市(上海)2019年度职工社会保险缴费基数上、下限分别调整为 23496 元和 4699 元,
//
//private const val minSocialSafety = 4279
//private const val maxSocialSafety = 21396
//本地默认值，社保基数区间，远端值未设置时 使用此值
const val minSocialSafety_DF = 5975
const val maxSocialSafety_DF = 31014

// 养老保险8% +医疗保险2% +失业保险0.5% = 10.5%
private const val socialSafetyPercent = "0.105"

//private const val minPublicMoney = 2190
//private const val maxPublicMoney = 19512
//2019年上海公积金7%比例上下限分别为 3290、334.上限对应工资基数：23496，下限对应工资基数：2385.71
//本地默认值，公积金基数区间，远端值未设置时 使用此值
const val minPublicMoney_DF = 4971
const val maxPublicMoney_DF = maxSocialSafety_DF

private const val publicMoneyPercent = "0.07"

private const val missCount = "0"//修正参数

//(以上海为例)计算险金扣除数
fun calculateWelfare(salaryVal: String): String {
    try {
        return when (salaryVal.toDouble()) {
            in 0..ConstGetter.getMinPublicMoney().toInt() -> //公积金下限、社保下限以下。
                add(multiply(ConstGetter.getMinPublicMoney().toString(), publicMoneyPercent, 2), multiply(ConstGetter.getMinSocialSafety().toString(), socialSafetyPercent, 2), missCount)
            in ConstGetter.getMinPublicMoney()..ConstGetter.getMinSocialSafety() -> //公积金7%、社保下限
                add(multiply(salaryVal, publicMoneyPercent, 2), multiply(ConstGetter.getMinSocialSafety().toString(), socialSafetyPercent, 2), missCount)
            in ConstGetter.getMinSocialSafety()..ConstGetter.getMaxPublicMoney() -> //公积金7%,社保10.5%
                add(multiply(salaryVal, publicMoneyPercent, 2), multiply(salaryVal, socialSafetyPercent, 2), missCount)
            in ConstGetter.getMaxPublicMoney()..ConstGetter.getMaxSocialSafety() -> //公积金上限,社保10.5%
                add(multiply(ConstGetter.getMaxPublicMoney().toString(), publicMoneyPercent, 2), multiply(salaryVal, socialSafetyPercent, 2), missCount)
            in ConstGetter.getMaxSocialSafety()..Int.MAX_VALUE -> {//公积金上限,社保上限
                add(multiply(ConstGetter.getMaxPublicMoney().toString(), publicMoneyPercent, 2), multiply(ConstGetter.getMaxSocialSafety().toString(), socialSafetyPercent, 2), missCount)
            }
            else -> "0"
        }
    } catch (e: Exception) {
        return "0"
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


/**
 * 幕布动画效果
 */
fun startActDrop(view: View, centerX: Int, centerY: Int, startRadius: Float, endRadius: Float, duration: Long = 500, endAnim: (() -> Unit)) {

    val animator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius)
    } else {
        endAnim.invoke()
        return
    }
    view.visibility = View.VISIBLE
    animator.duration = duration
    animator.interpolator = AccelerateInterpolator()
    animator.start()
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {

        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            view.visibility = View.GONE
        }
    })
    endAnim.invoke()
}

/**
 * 控件拖拽监听器
 */
interface OnDraggableClickListener {

    /**
     * 当控件拖拽完后回调
     *
     * @param v    拖拽控件
     * @param left 控件左边距
     * @param top  控件右边距
     */
    fun onDragged(v: View, left: Int, top: Int)

    /**
     * 当可拖拽控件被点击时回调
     *
     * @param v 拖拽控件
     */
    fun onClick(v: View)
}

fun dp2px(f: Float) = (f * BaseApp.instance.resources.displayMetrics.density + 0.5f).toInt()

@Synchronized
fun getScreenSize(): Point {
    val screenSize = Point()
    BaseApp.instance.windowManager.defaultDisplay.getSize(screenSize)
    return screenSize
}

@Synchronized
fun getBarHeight(): Int {
    val resourceId = BaseApp.instance.resources.getIdentifier("status_bar_height", "dimen", "android")
    return BaseApp.instance.resources.getDimensionPixelSize(resourceId)
}

fun getScreenW() = getScreenSize().x
fun getScreenH() = getScreenSize().y
fun getScreenHWithOutBar() = getScreenSize().y - getBarHeight()

fun getColorRes(@ColorRes colorRes: Int): Int = ContextCompat.getColor(BaseApp.instance, colorRes)
fun getDrawableRes(@DrawableRes res: Int): Drawable? = ContextCompat.getDrawable(BaseApp.instance, res)


fun startWebPage(url: String) {
    val intent = Intent(BaseApp.instance.applicationContext, WebViewActivity::class.java)
    intent.putExtra("url", url)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    BaseApp.instance.startActivity(intent)
}

fun canRequestPerm():Boolean{
    val isDeniedLimitDisable = System.currentTimeMillis() - getLong(sp_permission_denied_time) > limit_permission_time
    //默认无值是不进行权限申请的
    val savedIceTime = getLong(sp_permission_ice_time)
    if (savedIceTime <= 0){
        saveLong(sp_permission_ice_time, System.currentTimeMillis())
        return false
    }
    //冷却无效,超过了设定时间,代表需要重新进行权限申请了
    val isIceLimitDisable = System.currentTimeMillis() - getLong(sp_permission_ice_time) > limit_permission_open
    if (isIceLimitDisable){//刷新冷却时间
        saveLong(sp_permission_ice_time, System.currentTimeMillis())
    }
    return isDeniedLimitDisable && isIceLimitDisable
}