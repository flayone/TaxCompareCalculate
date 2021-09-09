package com.flayone.taxcc.taxcomparecalculate

import android.os.Bundle
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import kotlinx.android.synthetic.main.activity_web.*

class WebViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        wv_aw.settings.setSupportZoom(true)

        val url = intent.getStringExtra("url")
        if (url.isNullOrEmpty()) {
            showToast("无效链接")
            finish()
            return
        }
        wv_aw.loadUrl(url)
    }
}