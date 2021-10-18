package com.flayone.taxcc.taxcomparecalculate

import android.os.Bundle
import android.text.Html.fromHtml
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * 功能选择页：可选2019个税年累计算法、2018个税月均算法(税改收入对比)
 */
class HomeActivity : BaseActivity() {
    private val FORMAT_LINE = "<font color=\"#333333\">&#12288;&#12288;%s</span>"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        tips.text = fromHtml("<br><strong>" +
                String.format(FORMAT_LINE, "新个税计算器支持两种模式的算法，分别是年累计算法和月均计算法，当前适用的是年累计算法，也是新个税计算器首页默认采用的计算方法。两种算法具体区别参考下方描述：") + "</strong><br>" +
                String.format(FORMAT_LINE, "<strong>2019个税年累计算法（2019.1.1-至今）：</strong>每月预扣预缴、次年统算多退少补的计算方法。结果是前几个月的个税会很少，后续会越来越多。") + "<br>" +
                String.format(FORMAT_LINE, "<strong>2018个税月均算法（2018.10.1-2019.1.1）：</strong>每月单独计算个税,结果是每月个税固定不变。") + "<br>" +
                String.format(FORMAT_LINE, "<strong>两种算法的月个税会不一样，但收入不变的话年个税最终是一样的，不存在哪个多哪个少。</strong>")
        )
        year_cal.onClick {
            startAct(YearCalculateActivity())
        }
        month_cal.onClick {
            startAct(MainActivity())
        }
        old.onClick {
            startAct(MainActivity())
        }
    }
}