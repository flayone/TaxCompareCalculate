package com.flayone.taxcc.taxcomparecalculate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import java.io.*


/**
 * Created by liyayu on 2018/6/28.
 * 本地存储
 */
fun setPreferences(context: Context, preference: String, key: String, value: Boolean) {
    val editor = context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit()
    editor.putBoolean(key, value)
    editor.commit()
}

fun setPreferences(context: Context, preference: String, key: String, value: Int) {
    val editor = context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit()
    editor.putInt(key, value)
    editor.commit()
}

fun setPreferences(context: Context, preference: String, key: String, value: Long) {
    val editor = context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit()
    editor.putLong(key, value)
    editor.commit()
}

fun setPreferences(context: Context, preference: String, key: String, value: Float) {
    val editor = context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit()
    editor.putFloat(key, value)
    editor.commit()
}

fun setPreferences(context: Context, preference: String, key: String, value: String) {
    val editor = context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit()
    editor.putString(key, value)
    editor.commit()
}

fun setObjectPreferences(context: Context, preference: String, key: String, value: JSONConvertable) {
    val editor = context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit()
    editor.putString(key, value.toJSON())
//    ToastUtil.showToast(context, "value = " + value.toJSON())
    editor.commit()
}

fun getObjectPreference(context: Context, preference: String, key: String, defaultValue: JSONConvertable? = null): String {
    //    ToastUtil.showToast(context, "result = $result")
    return context.getSharedPreferences(preference, Context.MODE_PRIVATE).getString(key, defaultValue?.toJSON())
}

fun getPreference(context: Context, preference: String, key: String, defaultValue: Boolean = false): Boolean = context.getSharedPreferences(preference, Context.MODE_PRIVATE).getBoolean(key, defaultValue)

fun getPreference(context: Context, preference: String, key: String, defaultValue: Int = 0): Int =
        context.getSharedPreferences(preference, Context.MODE_PRIVATE).getInt(key, defaultValue)

fun getPreference(context: Context, preference: String, key: String, defaultValue: Long = 0): Long =
        context.getSharedPreferences(preference, Context.MODE_PRIVATE).getLong(key, defaultValue)

fun getPreference(context: Context, preference: String, key: String, defaultValue: Float = 0f): Float = context.getSharedPreferences(preference, Context.MODE_PRIVATE).getFloat(key, defaultValue)

fun getPreference(context: Context, preference: String, key: String, defaultValue: String = ""): String? = context.getSharedPreferences(preference, Context.MODE_PRIVATE).getString(key, defaultValue)

fun clearPreference(context: Context, preference: String) {
    val editor = context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit()
    editor.clear()
    editor.commit()
}

fun saveDrawable(context: Context, preference: String, res: Int, key: String) {
    val editor = context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit()
    val bitmap = BitmapFactory.decodeResource(context.resources, res)
    val bao = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 50, bao)
    val putString = String(android.util.Base64.encodeToString(bao.toByteArray(), android.util.Base64.DEFAULT).toCharArray())
    editor.putString(key, putString)
    editor.commit()
}

fun getDrawable(context: Context, preference: String, key: String): Drawable {
    val result = context.getSharedPreferences(preference, Context.MODE_PRIVATE).getString(key, "")
    val bais = ByteArrayInputStream(android.util.Base64.decode(result, android.util.Base64.DEFAULT))
    return Drawable.createFromStream(bais, "")
}

fun saveObject(context: Context, preference: String, key: String, data: Any) {
    if (data !is Serializable) return//data must implements Serializable
    val editor = context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit()
    val ba = ByteArrayOutputStream()
    try {
        val oos = ObjectOutputStream(ba)
        oos.writeObject(data)
        val putString = String(android.util.Base64.encodeToString(ba.toByteArray(), android.util.Base64.DEFAULT).toCharArray())
        editor.putString(key, putString)
        editor.commit()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getObject(context: Context, preference: String, key: String): Any? {
    val result = context.getSharedPreferences(preference, Context.MODE_PRIVATE).getString(key, "")
    val bais = ByteArrayInputStream(android.util.Base64.decode(result, android.util.Base64.DEFAULT))
    try {
        return ObjectInputStream(bais).readObject()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}