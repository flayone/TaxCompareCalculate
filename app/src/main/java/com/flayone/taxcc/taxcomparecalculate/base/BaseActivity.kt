package com.flayone.taxcc.taxcomparecalculate.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.flayone.taxcc.taxcomparecalculate.HomeActivity
import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.utils.MyLogger
import com.flayone.taxcc.taxcomparecalculate.utils.ToastUtil
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

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        preInit()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        preInit()
    }

    private fun preInit() {
        try {
            //设置默认的toolbar
            toolbar = findViewById(R.id.in_toolbar)
            switchButton = findViewById(R.id.mode_change)
            setSupportActionBar(toolbar)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        initView()
    }

    fun setTitle(s: String) {
        toolbar?.title = s
        switchButton?.visibility = View.VISIBLE
        switchButton?.onClick {
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
}