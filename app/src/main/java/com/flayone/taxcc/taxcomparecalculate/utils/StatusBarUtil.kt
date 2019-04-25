package com.flayone.taxcc.taxcomparecalculate.utils

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.flayone.taxcc.taxcomparecalculate.utils.OSUtil.isFlyme
import com.flayone.taxcc.taxcomparecalculate.utils.OSUtil.isMIUI
import com.readystatesoftware.systembartint.SystemBarTintManager

/**
 * 状态栏工具类
 * Created by liyayu on 2019/4/23.
 */
object StatusBarUtil {
    /**
     * 设置状态栏为透明
     * @param activity
     */
    @TargetApi(19)
    fun setTranslucentStatus(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = activity.window
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     * @param activity
     * @param colorId
     */
    fun setStatusBarColor(activity: Activity, colorId: Int) {

        //Android6.0（API 23）以上，系统方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.statusBarColor = activity.resources.getColor(colorId)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
            setTranslucentStatus(activity)
            //设置状态栏颜色
            val tintManager = SystemBarTintManager(activity)
            tintManager.isStatusBarTintEnabled = true
            tintManager.setStatusBarTintResource(colorId)
        }
    }

    /**
     * 设置状态栏模式
     * @param activity
     * @param isTextDark 文字、图标是否为黑色 （false为默认的白色）
     * @param colorId 状态栏颜色
     * @return
     */
    fun setStatusBarMode(activity: Activity, isTextDark: Boolean, colorId: Int) {

        if (!isTextDark) {
            //文字、图标颜色不变，只修改状态栏颜色
            setStatusBarColor(activity, colorId)
        } else {
            //修改状态栏颜色和文字图标颜色
            setStatusBarColor(activity, colorId)
            //4.4以上才可以改文字图标颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                when {
                    isMIUI() -> //小米MIUI系统
                        setMIUIStatusBarTextMode(activity, isTextDark)
                    isFlyme() -> //魅族flyme系统
                        setFlymeStatusBarTextMode(activity, isTextDark)
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        //6.0以上，调用系统方法
                        val window = activity.window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                    else -> {
                        //4.4以上6.0以下的其他系统，暂时没有修改状态栏的文字图标颜色的方法，有可以加上
                    }
                }
            }
        }
    }

    /**
     * 设置Flyme系统状态栏的文字图标颜色
     * @param activity
     * @param isDark 状态栏文字及图标是否为深色
     * @return
     */
    fun setFlymeStatusBarTextMode(activity: Activity, isDark: Boolean): Boolean {
        val window = activity.window
        var result = false
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                        .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (isDark) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }

    /**
     * 设置MIUI系统状态栏的文字图标颜色（MIUIV6以上）
     * @param activity
     * @param isDark 状态栏文字及图标是否为深色
     * @return
     */
    fun setMIUIStatusBarTextMode(activity: Activity, isDark: Boolean): Boolean {
        var result = false
        val window = activity.window
        if (window != null) {
            val clazz = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                if (isDark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
                }
                result = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (isDark) {
                        activity.window.decorView.systemUiVisibility = View
                                .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View
                                .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        activity.window.decorView.systemUiVisibility = View
                                .SYSTEM_UI_FLAG_VISIBLE
                    }
                }
            } catch (e: Exception) {

            }

        }
        return result
    }
}