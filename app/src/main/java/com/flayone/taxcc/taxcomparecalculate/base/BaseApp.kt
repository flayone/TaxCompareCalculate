package com.flayone.taxcc.taxcomparecalculate.base

import android.app.Application
import com.dbflow5.config.FlowConfig
import com.dbflow5.config.FlowManager
import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.YearCalculateActivity
import com.flayone.taxcc.taxcomparecalculate.ad.AdvanceAD
import com.flayone.taxcc.taxcomparecalculate.ad.GroMoreAD
import com.flayone.taxcc.taxcomparecalculate.utils.LogUtil
import com.flayone.taxcc.taxcomparecalculate.utils.getLong
import com.flayone.taxcc.taxcomparecalculate.utils.saveLong
import com.flayone.taxcc.taxcomparecalculate.utils.sp_user_first_start_time
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.BuildConfig
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.umeng.commonsdk.UMConfigure


class BaseApp : Application() {
    companion object {
        lateinit var instance: BaseApp //延迟加载，不需要初始化，否则需要在构造函数初始化
    }

    private var hasSDKInit = false

    private val debug = BuildConfig.DEBUG
    private val umKey = "6155303814e22b6a4f12b200"

    private val TAG = "BaseApp"

    override fun onCreate() {
        super.onCreate()
        instance = this
        FlowManager.init(FlowConfig.builder(this).build())

        val formatStrategy = PrettyFormatStrategy.newBuilder().tag("lyy").build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        // SDK预初始化函数不会采集设备信息，也不会向友盟后台上报数据。
// preInit预初始化函数耗时极少，不会影响App首次冷启动用户体验
        UMConfigure.preInit(this, umKey, null)


        if (getLong(sp_user_first_start_time) < 0) {
            saveLong(sp_user_first_start_time, System.currentTimeMillis())
        }
    }

    public fun initSDK() {
        if (hasSDKInit) {
            LogUtil.d("[$TAG] hasSDKInit")
            return
        }
        /**
         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
         */
        Beta.defaultBannerId = R.mipmap.update

        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
         * 不设置会默认所有activity都可以显示弹窗;
         */
        Beta.canShowUpgradeActs.add(YearCalculateActivity::class.java)
        Bugly.init(this, "a122529811", debug)

//        AdvanceAD.initAD(this)
        GroMoreAD().initSDK(this)
        //初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
        UMConfigure.init(this, umKey, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "")
        hasSDKInit = true
    }
}