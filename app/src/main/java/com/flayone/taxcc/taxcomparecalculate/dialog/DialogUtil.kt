package com.flayone.taxcc.taxcomparecalculate.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Point
import android.os.IBinder
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.base.BaseApp
import com.flayone.taxcc.taxcomparecalculate.utils.*
import com.flayone.taxcc.taxcomparecalculate.widget.RippleEffect
import kotlinx.android.synthetic.main.dialog_custom_parameters.*
import org.jetbrains.anko.windowManager

/**
 * layoutId 布局id  widthPercent，heightPercent 分别是宽高相对屏幕的百分比，默认0.5代表各一半
 */
abstract class BaseKtLayoutDialog @JvmOverloads constructor(context: Context, @LayoutRes private val layoutId: Int, private val widthPercent: Float = 0.5f, private val heightPercent: Float = 0.5f, themeStyle: Int = 0) : Dialog(context, themeStyle) {
    init {
        this.setContentView(layoutId)
    }

    override fun show() {
        super.show()
        /**
         * 设置具体高宽，要设置在show的后面
         */
        window?.run {

            val layoutParams =  attributes
            val screenSize = Point()
            BaseApp.instance.windowManager.defaultDisplay.getSize(screenSize)
            val screenWidth = screenSize.x
            val screenHeight = screenSize.y
            layoutParams.gravity = Gravity.CENTER
            layoutParams.width = multiply(screenWidth.toString(), widthPercent.toString()).toDouble().toInt()
            layoutParams.height = multiply(screenHeight.toString(), heightPercent.toString()).toDouble().toInt()
//        window.decorView.setPadding(0, 0, 0, 0)
             attributes = layoutParams
        }
    }

    override fun dismiss() {
        hideDialogKeyboard(this)
        super.dismiss()
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        hideDialogKeyboard(this)
        super.setOnDismissListener(listener)
    }
}


private fun hideDialogKeyboard(dialog: Dialog) = hideKeyboard(dialog.window?.decorView?.windowToken)

/**
 * 获取InputMethodManager，隐藏软键盘
 */
private fun hideKeyboard(token: IBinder?) {
    val im = BaseApp.instance.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
}

/**
 * messageString : 提示框内容
 * doEnsure：确认按钮回调
 */
@JvmOverloads
fun showAlertDialog(context: Context, messageString: String, doEnsure: () -> Unit, doCancel: () -> Unit = {}, titleString: String = "提示") {
    val builder = AlertDialog.Builder(context)
    builder.apply {
        setTitle(titleString)
        setMessage(messageString)
        setPositiveButton("确认") { dialog, _ ->
            doEnsure.invoke()
            dialog.dismiss()
        }
        setNegativeButton("取消") { dialog: DialogInterface, _: Int ->
            doCancel.invoke()
            dialog.dismiss()
        }
    }

    val alertDialog = builder.show()
    alertDialog.setCanceledOnTouchOutside(true)
}

//用户自定义缴税参数dialog
class CustomParametersDialog(context: Context, titleString: String, model: BaseCalculateModel, doEnsure: BaseCalculateModel.() -> Unit) :
        BaseKtLayoutDialog(context, R.layout.dialog_custom_parameters, 0.9f, 0.5f) {
    private val listener = object : CustomSalaryListener {
        override fun ensure(da: BaseCalculateModel) {
            dismiss()
            doEnsure.invoke(da)
        }

    }

    init {
        keepEditTwoPoint(cet_salary)
        keepEditTwoPoint(cet_welfare)
        keepEditTwoPoint(cet_expend)
        cet_salary.setText(model.baseSalary)
        cet_welfare.setText(model.welfare)
        cet_expend.setText(model.expend)

        RippleEffect().get().setViewRipple(tx_ensure, tx_cancel)
        tx_title.text = titleString
        //自动计算险金福利
        cet_salary.addTextChangedListener(MyTextWatcher(object : BaseEnsureListener {
            override fun ensure(s: String) {
                if (s.isEmpty()) {
                    cet_welfare.setText("")
                } else {
                    cet_welfare.setText(calculateWelfare(s))
                }
            }

        }))
        tx_cancel.setOnClickListener { dismiss() }
        tx_ensure.setOnClickListener {
            model.apply {
                baseSalary = cet_salary.text.toString().trim()
                welfare = cet_welfare.text.toString().trim()
                expend = cet_expend.text.toString().trim()

                needHistorySynchronized = cb_sync.isChecked
                when {
                    baseSalary.isEmpty() -> showTips("税前收入为空,是否继续?", this)
                    welfare.isEmpty() -> showTips("险金扣除数为空,是否继续?", this)
                    expend.isEmpty() -> showTips("附加扣除数为空,是否继续?", this)
                    else -> listener.ensure(this)
                }
            }
        }

    }

    private fun showTips(tips: String, model: BaseCalculateModel) {
        showAlertDialog(context, tips, { listener.ensure(model) })
    }
}