package com.flayone.taxcc.taxcomparecalculate

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import com.flayone.taxcc.taxcomparecalculate.ad.AdvanceAD
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import com.flayone.taxcc.taxcomparecalculate.base.BaseApp
import com.flayone.taxcc.taxcomparecalculate.dialog.UserPrivacyDialog
import com.flayone.taxcc.taxcomparecalculate.utils.*
import com.flayone.taxcc.taxcomparecalculate.utils.ConstGetter.getADWait
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.huawei.agconnect.remoteconfig.AGConnectConfig
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
//            val cal = object : OnPermissionCallback {
//                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
//                    d("[checkPermission] onGranted")
//                    startSDK()
//                }
//
//                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
//                    d("[checkPermission] onDenied")
//                    saveLong(sp_permission_denied_time, System.currentTimeMillis())
//                    startSDK()
//                }
//            }
//
//            if (canRequestPerm()) {
//                XXPermissions.with(this).permission(Permission.READ_PHONE_STATE)?.request(cal)
//            } else {
                startSDK()
//            }
        } else {
            d("[checkPermission] no need")
            startSDK()
        }
    }


    /**
     * 开始初始化、主要是第三方服务和广告
     */
    private fun startSDK() {
        d("[startSDK]")
        //初始化第三方SDK
        BaseApp.instance.initSDK()

        //满足一定条件才会请求展示广告
        if (isUserTimeEnough()) {
            getSplashAD()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                goMain()
            }, 1200)
        }
    }

    private fun getSplashAD() {
        //请求开屏广告
        val callBack = object : AdvanceAD.SplashCallBack {
            override fun adEnd() {
//                提前初始化变量信息
                initAGConfig()
            }

            override fun jumpMain() {
                goMain()
            }

        }
        AdvanceAD(this).loadSplash(fl_ad,  callBack)
    }

    private fun initAGConfig() {

        try {
            val config = AGConnectConfig.getInstance()
            val map = mutableMapOf<String, Any>()
            map[AGKEY_MINSS] = minSocialSafety_DF
            map[AGKEY_MAXSS] = maxSocialSafety_DF
            map[AGKEY_MINPM] = minPublicMoney_DF
            map[AGKEY_MAXPM] = maxPublicMoney_DF
            map[AGKEY_LIMIT_AD_TIME] = limit_ad_time
            //设置默认值
            config.applyDefault(map)

            config.apply(config.loadLastFetched())
            //拉取服务端值
            //fetch()接口默认更新周期为 12 小时，fetch(long intervalSeconds)接口则可以自行设置更新周期，在更新周期内再次调用fetch接口不会去云侧同步数据，而是使用本地缓存数据直接返回。
            //fetch(long intervalSeconds)间隔时间单位为秒，在调测阶段可以设置为0，方便调测，在实际生产阶段建议设置较大值，防止受到流控限制。
            val fetch = if (BuildConfig.DEBUG) {
                config.fetch(0)
            } else {
                config.fetch()
            }
            fetch.addOnSuccessListener {
                config.apply(it)
                //使用配置值
            }.addOnFailureListener {

            }
            val vl = config.mergedAll


            d("ag config inf : vl= $vl  ")
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    /**
     * 跳转首页
     */
    private fun goMain() {
        startAct(YearCalculateActivity())
        finish()
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    /**
     * todo 是否用户第一次不展示广告？
     * 目前逻辑为：用户第一次打开时可能还未获取到服务端远程配置信息，且默认值未初始化，此时getADWait获取的值为0，导致第一次一定会出广告
     */
    private fun isUserTimeEnough(): Boolean {
        val result=  if (getLong(sp_user_first_start_time) < 0) {
            saveLong(sp_user_first_start_time, System.currentTimeMillis())
            false
        } else {
            System.currentTimeMillis() - getLong(sp_user_first_start_time) > getADWait()//指定时间以上才用加载广告
        }
        d("isUserTimeEnough = $result")
        return true
    }

}