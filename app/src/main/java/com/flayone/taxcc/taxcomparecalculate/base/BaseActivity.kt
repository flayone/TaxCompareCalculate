package com.flayone.taxcc.taxcomparecalculate.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.flayone.taxcc.taxcomparecalculate.HomeActivity
import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.YearCalculateActivity
import com.flayone.taxcc.taxcomparecalculate.dialog.UserPrivacyDialog
import com.flayone.taxcc.taxcomparecalculate.utils.MyLogger
import com.flayone.taxcc.taxcomparecalculate.utils.StatusBarUtil.setStatusBarMode
import com.flayone.taxcc.taxcomparecalculate.utils.ToastUtil
import com.flayone.taxcc.taxcomparecalculate.utils.getBoole
import com.flayone.taxcc.taxcomparecalculate.utils.sp_user_privacy
import com.flayone.taxcc.taxcomparecalculate.widget.RippleEffect
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.lang.Exception

@SuppressLint("Registered")
open
/**
 * Created by liyayu on 2018/6/22.
 */
class BaseActivity : AppCompatActivity(), MyLogger {
    var toolbar: Toolbar? = null
    var switchButton: TextView? = null
    private var pressedTime = 0L //按下返回键时间，用来判断可否退出

    override fun onContentChanged() {
        super.onContentChanged()
        preInit()
    }

    override fun onResume() {
        super.onResume()
        if (!getBoole(sp_user_privacy)) {
            UserPrivacyDialog(this).show()
        }
    }

    private fun preInit() {
        try {
            //设置默认的toolbar
            toolbar = findViewById(R.id.toolbar)
            switchButton = findViewById(R.id.mode_change)
            setSupportActionBar(toolbar)
            supportActionBar?.setHomeButtonEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        initView()
    }

    fun setTitle(s: String) {
        supportActionBar?.title = s
        switchButton?.visibility = View.VISIBLE
        switchButton?.onClick {
            finish()
            startAct(HomeActivity())
        }
    }


    open fun initView() {}

    fun showToast(s: String) {
        ToastUtil.showToast(this, s)
    }

    fun startAct(activity: Activity) {
        startAct(activity::class.java)
    }

    fun startAct(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }

    //带动画启动activity
    fun startAnimAct(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(Intent(this, activity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startAct(activity)
        }
    }

    //状态栏背景、字体颜色
    @JvmOverloads
    fun setStatusColor(res: Int, isTextBlack: Boolean = false) {
        setStatusBarMode(this, isTextBlack, res)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v!!.windowToken)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * @param v
     * @param event
     * @return
     */
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && (v is EditText || v is TextInputEditText)) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private fun hideKeyboard(token: IBinder?) {
        if (token != null) {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    @JvmOverloads
    protected fun ripple(view: View, cornerRadius: Float = 0f) {
        RippleEffect().get().setViewRipple(view, cornerRadius)
    }

    protected fun ripple(vararg views: View) {
        for (view in views) {
            RippleEffect().get().setViewRipple(view)
        }
    }

    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (now - pressedTime > 1500 && (this is YearCalculateActivity || this is HomeActivity)) {
            pressedTime = now
            showToast("再次点击退出")
        } else {
            super.onBackPressed()
        }
    }
}