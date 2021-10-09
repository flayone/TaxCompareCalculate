package com.flayone.taxcc.taxcomparecalculate

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.flayone.taxcc.taxcomparecalculate.ad.AdvanceAD
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import com.flayone.taxcc.taxcomparecalculate.base.BaseApp
import com.flayone.taxcc.taxcomparecalculate.dialog.UserPrivacyDialog
import com.flayone.taxcc.taxcomparecalculate.utils.getBoole
import com.flayone.taxcc.taxcomparecalculate.utils.sp_user_privacy
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        actionBar?.hide()

        setContentView(R.layout.activity_welcome)

        if (!getBoole(sp_user_privacy)) {
            UserPrivacyDialog(this) {
                checkPermission()
            }.show()
        } else {
            checkPermission()
        }

    }

    /**
     * 注意！：由于工信部对设备权限等隐私权限要求愈加严格，强烈推荐APP提前申请好权限，且用户同意隐私政策后再加载广告
     */
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT in 23..28) {
            val cal = object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    d("[checkPermission] onGranted")
                    startSDK()
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    d("[checkPermission] onDenied")
                    startSDK()
                }
            }
            XXPermissions.with(this).permission(Permission.READ_PHONE_STATE)?.request(cal)
        } else {
            d("[checkPermission] no need")
            startSDK()
        }
    }


    /**
     * 开始初始化、主要是第三方服务和广告
     */
    private fun startSDK() {
        d("[startInit]")
        //初始化
        BaseApp.instance.initSDK()
        getSplashAD()
    }

    private fun getSplashAD() {
        //请求开屏广告
        AdvanceAD(this).loadSplash(fl_ad, null, null) {
            startAct(YearCalculateActivity())
            finish()
        }
    }


}