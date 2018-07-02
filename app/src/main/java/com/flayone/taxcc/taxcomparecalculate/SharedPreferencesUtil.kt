package com.flayone.taxcc.taxcomparecalculate

import android.content.Context



/**
 * Created by liyayu on 2018/6/28.
 * 本地存储
 */
fun setPreferences(context: Context?, preference: String, key: String, value: Boolean) {
    if (context == null) return
    val sharedPreferences = context!!.getSharedPreferences(preference, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean(key, value)
    editor.commit()
}

fun setPreferences(context: Context?, preference: String, key: String, value: Int) {
    if (context == null) return
    val sharedPreferences = context!!.getSharedPreferences(preference, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt(key, value)
    editor.commit()
}

fun setPreferences(context: Context?, preference: String, key: String, value: Long) {
    if (context == null) return
    val sharedPreferences = context!!.getSharedPreferences(preference, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putLong(key, value)
    editor.commit()
}

fun setPreferences(context: Context?, preference: String, key: String, value: Float) {
    if (context == null) return
    val sharedPreferences = context!!.getSharedPreferences(preference, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putFloat(key, value)
    editor.commit()
}

fun setPreferences(context: Context?, preference: String, key: String, value: String) {
    if (context == null) return
    val sharedPreferences = context!!.getSharedPreferences(preference, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(key, value)
    editor.commit()
}


fun getPreference(context: Context?, preference: String, key: String, defaultValue: Boolean): Boolean {
    val sharedPreferences = context!!.getSharedPreferences(preference, Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean(key, defaultValue)
}

fun getPreference(context: Context?, preference: String, key: String, defaultValue: Int): Int {
    val sharedPreferences = context!!.getSharedPreferences(preference, Context.MODE_PRIVATE)
    return sharedPreferences.getInt(key, defaultValue)
}

fun getPreference(context: Context?, preference: String, key: String, defaultValue: Long): Long {
    val sharedPreferences = context!!.getSharedPreferences(preference, Context.MODE_PRIVATE)
    return sharedPreferences.getLong(key, defaultValue)
}

fun getPreference(context: Context?, preference: String, key: String, defaultValue: Float): Float {
    val sharedPreferences = context!!.getSharedPreferences(preference, Context.MODE_PRIVATE)
    return sharedPreferences.getFloat(key, defaultValue)
}

fun getPreference(context: Context?, preference: String, key: String, defaultValue: String): String? {
    val sharedPreferences = context!!.getSharedPreferences(preference, Context.MODE_PRIVATE)
    return sharedPreferences.getString(key, defaultValue)
}


fun clearPreference(context: Context, preference: String) {
    val sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.clear()
    editor.commit()
}