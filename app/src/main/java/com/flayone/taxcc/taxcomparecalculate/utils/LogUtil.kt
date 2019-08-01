package com.flayone.taxcc.taxcomparecalculate.utils

import android.content.Context
import androidx.annotation.NonNull
import android.text.TextUtils
import android.util.Log
import com.flayone.taxcc.taxcomparecalculate.BuildConfig


object LogUtil {
    /**
     * 默认的文库日志Tag标签
     */
    val DEFAULT_TAG = "LogUtil"

    /**
     * 此常量用于控制是否打日志到Logcat中 release版本中本变量应置为false
     */

    private val LOGGABLE = BuildConfig.DEBUG


    /**
     * 打印debug级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    fun d(tag: String, str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.d(tag, str)
                }
            })
        }
    }

    /**
     * 打印debug级别的log
     *
     * @param str 内容
     */
    fun d(str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.d(DEFAULT_TAG, str)
                }
            })
        }
    }

    /**
     * 打印超长log
     *
     * @param str      超长log字符串
     * @param callBack 打印回调
     */
    private fun moreLog(str: String, callBack: LogCallBack) {
        var str = str
        //系统log 默认最大 4 * 1024 ，这里稍微缩小大小
        val segmentSize = 4 * 1000
        val length = str.length.toLong()
        if (length <= segmentSize) {// 长度小于等于限制直接打印
            callBack.logCallBackFun(str)
        } else {
            while (str.length > segmentSize) {// 循环分段打印日志
                val logContent = str.substring(0, segmentSize)
                str = str.replace(logContent, "")
                callBack.logCallBackFun(logContent)
            }
            callBack.logCallBackFun(str)// 打印剩余日志
        }
    }

    /**
     * 打印warning级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    fun w(tag: String, str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.w(tag, str)
                }
            })
        }
    }

    /**
     * 打印warning级别的log
     *
     * @param str 内容
     */
    fun w(str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.w(DEFAULT_TAG, str)
                }
            })
        }
    }

    /**
     * 打印error级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    fun e(tag: String, str: String, tr: Throwable) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.e(tag, str)
                    tr.printStackTrace()
                }
            })
        }
    }

    /**
     * 打印error级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    fun e(tag: String, str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.e(tag, str)
                }
            })
        }
    }

    /**
     * 打印error级别的log
     *
     * @param str 内容
     */
    fun e(str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.e(DEFAULT_TAG, str)
                }
            })
        }
    }

    /**
     * 打印error级别的log
     */
    fun e(e: Throwable) {
        if (LOGGABLE) {
            moreLog(e.toString(), object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.e(DEFAULT_TAG, null, e)
                }
            })
        }
    }

    /**
     * 打印error级别的log
     */
    fun e(e: Throwable, str: String) {
        if (LOGGABLE) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.e(DEFAULT_TAG, str, e)
                }
            })
        }
    }

    /**
     * 打印info级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    fun i(tag: String, str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.i(tag, str)
                }
            })
        }
    }

    /**
     * 打印info级别的log
     *
     * @param str 内容
     */
    fun i(str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.i(DEFAULT_TAG, str)
                }
            })
        }
    }

    /**
     * 打印verbose级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    fun v(tag: String, str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.v(tag, str)
                }
            })
        }
    }

    /**
     * 打印verbose级别的log
     *
     * @param str 内容
     */
    fun v(str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            moreLog(str, object : LogCallBack {
                override fun logCallBackFun(@NonNull str: String) {
                    Log.v(DEFAULT_TAG, str)
                }
            })
        }
    }

    /**
     * 将log写入文件(/data/data/package name/files/log)
     *
     * @param str 内容
     */
    fun flood(context: Context, str: String) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            //			str += "\n";
            //			FileUtils.writeToFile(context, str.getBytes(), "/log", true);
        }
    }

    internal interface LogCallBack {
        fun logCallBackFun(str: String)
    }
}