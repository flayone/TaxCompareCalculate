package com.flayone.taxcc.taxcomparecalculate.base

import android.app.Application
import com.dbflow5.config.FlowConfig
import com.dbflow5.config.FlowManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

class BaseApp :Application(){
    companion object {
        lateinit var instance: BaseApp //延迟加载，不需要初始化，否则需要在构造函数初始化
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FlowManager.init(FlowConfig.builder(this).build())

        val formatStrategy = PrettyFormatStrategy.newBuilder().tag("lyy").build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }
}