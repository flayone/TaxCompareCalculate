package com.flayone.taxcc.taxcomparecalculate.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.customview.widget.ViewDragHelper
import com.advance.utils.ScreenUtil
import com.flayone.taxcc.taxcomparecalculate.*
import com.flayone.taxcc.taxcomparecalculate.dialog.UserPrivacyDialog
import com.flayone.taxcc.taxcomparecalculate.utils.MyLogger
import com.flayone.taxcc.taxcomparecalculate.utils.StatusBarUtil.setStatusBarMode
import com.flayone.taxcc.taxcomparecalculate.utils.ToastUtil
import com.flayone.taxcc.taxcomparecalculate.utils.getBoole
import com.flayone.taxcc.taxcomparecalculate.utils.sp_user_privacy
import com.flayone.taxcc.taxcomparecalculate.widget.RippleEffect
import com.flayone.taxcc.taxcomparecalculate.widget.XActionBarDrawerToggle
import com.flayone.taxcc.taxcomparecalculate.widget.XDrawerLayout
import com.google.android.material.textfield.TextInputEditText
import kotlin.system.exitProcess


@SuppressLint("Registered")
open
/**
 * Created by liyayu on 2018/6/22.
 */
class BaseActivity : AppCompatActivity(), MyLogger {
    var toolbar: Toolbar? = null
    var settingButton: TextView? = null
    private var pressedTime = 0L //按下返回键时间，用来判断可否退出
    var mDrawerLayout: XDrawerLayout? = null
    override fun onContentChanged() {
        super.onContentChanged()
        initToolBar()
    }

    override fun onResume() {
        super.onResume()
        if (!getBoole(sp_user_privacy)) {
            //如果是欢迎页逻辑交由此页面处理
            if (this is WelcomeActivity || this is WebViewActivity) {
                return
            }
            UserPrivacyDialog(this) {

            }.show()
        }
    }

    private fun initToolBar() {
        try {
            //设置默认的toolbar
            toolbar = findViewById(R.id.toolbar)
            settingButton = findViewById(R.id.mode_change)
            setSupportActionBar(toolbar)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        initView()
    }

    fun initDraw() {
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mDrawerLayout = findViewById(R.id.drawer_layout_root)
//            supportActionBar?.setDisplayShowHomeEnabled(true)
//创建返回键，并实现打开关/闭监听

        //创建返回键，并实现打开关/闭监听
        val mDrawerToggle = object : XActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }
        }
        mDrawerToggle.syncState()
        mDrawerLayout?.run {
            addDrawerListener(mDrawerToggle)
            setCustomLeftEdgeSize(this, 1f)
        }
    }

    open fun setCustomLeftEdgeSize(drawerLayout: XDrawerLayout, displayWidthPercentage: Float) {
        try {
            // find ViewDragHelper and set it accessible
            val leftDraggerField = drawerLayout.javaClass.getDeclaredField("mLeftDragger")
                    ?: return
            leftDraggerField.isAccessible = true
            val leftDragger = leftDraggerField[drawerLayout] as ViewDragHelper
            // find edgesize and set is accessible
            val edgeSizeField = leftDragger.javaClass.getDeclaredField("mEdgeSize")
            edgeSizeField.isAccessible = true
            val edgeSize = edgeSizeField.getInt(leftDragger)
            d("mEdgeSize == $edgeSize")
            // set new edgesize
            val widthPixels: Int = ScreenUtil.getScreenWidth(this)
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (widthPixels * displayWidthPercentage).toInt()))
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    fun setTitle(s: String) {
        supportActionBar?.title = s
//        settingButton?.visibility = View.VISIBLE
//        settingButton?.onClick {
//            finish()
//            startAct(HomeActivity())
//        }
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

    //返回键监听
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode === KeyEvent.KEYCODE_MENU) {
            mDrawerLayout?.run {
                if (isDrawerOpen(Gravity.LEFT)) {
                    closeDrawer(Gravity.LEFT)
                } else {
                    openDrawer(Gravity.LEFT)
                }
            }
            return true
        } else if (event.keyCode === KeyEvent.KEYCODE_BACK) {
            mDrawerLayout?.run {
                if (isDrawerOpen(Gravity.LEFT)) {
                    closeDrawer(Gravity.LEFT)
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (this is YearCalculateActivity) {
            if (now - pressedTime > 1500) {
                pressedTime = now
                showToast("再次点击退出")
            } else {//退出应用
                exitProcess(0)
            }
        } else {
            super.onBackPressed()
        }
    }
}