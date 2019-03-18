package com.flayone.taxcc.taxcomparecalculate.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet


class IconFontTextView : android.support.v7.widget.AppCompatTextView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    fun init(context: Context) {
        //加载字体文件
        val tp = IconFontTypeFace.getTypeface(context)
        this.typeface = tp
        //去掉padding,这样iconfont和普通字体容易对齐
        includeFontPadding = false
    }

    object IconFontTypeFace {
        //用static,整个app共用整个typeface就够了
        private var ttfTypeface: Typeface? = null

        @Synchronized
        internal fun getTypeface(context: Context): Typeface? {
            if (ttfTypeface == null) {
                try {
                    //context.getApplicationContext()防止内存泄漏
                    ttfTypeface = Typeface.createFromAsset(context.applicationContext.assets, "font/iconfont.ttf")
                } catch (ignored: Exception) {
                }

            }
            return ttfTypeface
        }

        @Synchronized
        fun clearTypeface() {
            ttfTypeface = null
        }
    }


}