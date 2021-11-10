package com.flayone.taxcc.taxcomparecalculate.utils

import com.huawei.agconnect.remoteconfig.AGConnectConfig

object ConstGetter {
    private val config = AGConnectConfig.getInstance()

    fun getMinSocialSafety(): Long {
        return getLongResult(AGKEY_MINSS)
    }

    fun getMaxSocialSafety(): Long {
        return getLongResult(AGKEY_MAXSS)
    }

    fun getMinPublicMoney(): Long {
        return getLongResult(AGKEY_MINPM)
    }

    fun getMaxPublicMoney(): Long {
        return getLongResult(AGKEY_MAXPM)
    }

    /**
     * 广告延迟触发时间
     */
    fun getADWait(): Long {
        return getLongResult(AGKEY_LIMIT_AD_TIME)
    }

    private fun getLongResult(key: String): Long {
        val result = config.getValueAsLong(key)
        val source = config.getSource(key)
        LogUtil.d("[getLongResult] key = $key ; result = $result ; source = $source")
        return result
    }
}