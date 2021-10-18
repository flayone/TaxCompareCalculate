package com.flayone.taxcc.taxcomparecalculate

import android.os.Bundle
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val version = BuildConfig.VERSION_NAME
        tv_aa_version.text = "V $version"


    }
}