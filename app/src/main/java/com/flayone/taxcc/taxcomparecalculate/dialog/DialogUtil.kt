package com.flayone.taxcc.taxcomparecalculate.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Point
import android.os.IBinder
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.base.BaseApp
import com.flayone.taxcc.taxcomparecalculate.utils.*
import com.flayone.taxcc.taxcomparecalculate.widget.RippleEffect
import com.flayone.taxcc.taxcomparecalculate.widget.UrlClickSpan
import kotlinx.android.synthetic.main.dialog_custom_parameters.*
import kotlinx.android.synthetic.main.dialog_user_privacy.*
import org.jetbrains.anko.windowManager
import kotlin.system.exitProcess

/**
 * layoutId 布局id  widthPercent，heightPercent 分别是宽高相对屏幕的百分比，默认0.5代表各一半
 */
abstract class BaseKtLayoutDialog @JvmOverloads constructor(context: Context, @LayoutRes private val layoutId: Int, private val widthPercent: Float = 0.5f, private val heightPercent: Float = -1f, themeStyle: Int = 0) : Dialog(context, themeStyle) {
    init {
        this.setContentView(layoutId)
    }

    override fun show() {
        super.show()
        /**
         * 设置具体高宽，要设置在show的后面
         */
        window?.run {

            val layoutParams = attributes
            val screenSize = Point()
            BaseApp.instance.windowManager.defaultDisplay.getSize(screenSize)
            val screenWidth = screenSize.x
            val screenHeight = screenSize.y
            layoutParams.gravity = Gravity.CENTER
            layoutParams.width = multiply(screenWidth.toString(), widthPercent.toString()).toDouble().toInt()
            if (heightPercent <= 0) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                layoutParams.height = multiply(screenHeight.toString(), heightPercent.toString()).toDouble().toInt()
            }
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


class UserPrivacyDialog(context: Context, ok: () -> Unit = {}) : BaseKtLayoutDialog(context, R.layout.dialog_user_privacy, 0.85f) {

    init {
        val content = "请你务必审慎阅读、充分理解“服务协议和隐私政策”个条款，包括但不限于：为了向你提供内容等服务，我们需要收集你的设备信息、操作日志等个人信息。你可以在“设置”中查看、变更、删除个人信息并管理你的授权。你可以阅读"
        val userInf = "《用户协议》"
        val and = "和"
        val privacyInf = "《隐私政策》"
        val end = "了解详细信息。如你同意，请点击“同意”开始接受我们的服务。"


        val userSpan = SpannableString(userInf)
        userSpan.setSpan(UrlClickSpan(USER_URL), 0, userInf.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        val privacySpan = SpannableString(privacyInf)
        privacySpan.setSpan(UrlClickSpan(PRIVACY_URL), 0, userInf.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        tv_dup_content.append(content)
        tv_dup_content.append(userSpan)
        tv_dup_content.append(and)
        tv_dup_content.append(privacySpan)
        tv_dup_content.append(end)

        tv_dup_content.movementMethod = LinkMovementMethod.getInstance()

        tv_dup_no.setOnClickListener {
            exitProcess(0)
        }

        tv_dup_yes.setOnClickListener {
            saveBoole(sp_user_privacy, true)
            dismiss()
            ok.invoke()
        }
        setCanceledOnTouchOutside(false)
        setCancelable(false)

    }

}