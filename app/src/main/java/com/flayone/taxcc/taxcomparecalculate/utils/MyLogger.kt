package com.flayone.taxcc.taxcomparecalculate.utils


interface MyLogger {
    companion object {
        fun cd(obj: Any, msg: Any?) {
            LogUtil.d("${obj::class.java.simpleName}\n", msg.toString())
        }

        fun ce(obj: Any, msg: Any?) {
            LogUtil.e("${obj::class.java.simpleName}\n", msg.toString())
        }
    }

    fun d(msg: Any?) {
        LogUtil.d("${this::class.java.simpleName}\n", msg.toString())
    }

    fun e(msg: Any?) {
        LogUtil.e("${this::class.java.simpleName}\n", msg.toString())
    }
}