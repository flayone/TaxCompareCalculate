package com.flayone.taxcc.taxcomparecalculate.utils

import com.flayone.taxcc.taxcomparecalculate.base.BaseApp
import com.huawei.agconnect.remoteconfig.AGConnectConfig

object ConstGetter {
    private val config = AGConnectConfig.getInstance()

    fun getMinSocialSafety(): Long {
        return getResult(AGKEY_MINSS)
    }

    fun getMaxSocialSafety(): Long {
        return getResult(AGKEY_MAXSS)
    }

    fun getMinPublicMoney(): Long {
        return getResult(AGKEY_MINPM)
    }

    fun getMaxPublicMoney(): Long {
        return getResult(AGKEY_MAXPM)
    }

    private fun getResult(key: String): Long {
        val result = config.getValueAsLong(key)
        val source = config.getSource(key)
        LogUtil.d("[getResult] key = $key ; result = $result ; source = $source")
        return result
    }
}