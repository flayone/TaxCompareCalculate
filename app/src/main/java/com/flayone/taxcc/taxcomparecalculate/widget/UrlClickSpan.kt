package com.flayone.taxcc.taxcomparecalculate.widget

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.base.BaseApp
import com.flayone.taxcc.taxcomparecalculate.utils.startWebPage


class UrlClickSpan(private val url: String) : ClickableSpan() {
    override fun onClick(widget: View) {
        startWebPage(url)
    }


    override fun updateDrawState(ds: TextPaint) {
        ds.color = BaseApp.instance.resources.getColor(R.color.mainColor)
        ds.isUnderlineText = false
    }
}