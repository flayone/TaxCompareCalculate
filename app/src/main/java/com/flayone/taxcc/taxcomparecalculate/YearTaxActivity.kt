package com.flayone.taxcc.taxcomparecalculate

import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.View
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import com.flayone.taxcc.taxcomparecalculate.utils.*
import kotlinx.android.synthetic.main.activity_year_tax.*

/**
 * Created by liyayu on 2019/4/23.
 * 计算年终奖的个人所得税
 */
class YearTaxActivity : BaseActivity() {

    private val duration = 550L
    private val mainColor = R.color.y_bg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_year_tax)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition = Fade().setDuration(duration)
            window.exitTransition = Fade().setDuration(2 * duration / 3)
        }
        supportActionBar?.run {
            title = "年终奖计算器"
            setBackgroundDrawable(getDrawableRes(mainColor))
        }
        setStatusColor(mainColor)
        isResultShow(false)
        ripple(btn_calculate, btn_calculate_original)
        keepEditTwoPoint(et_salary)

        et_salary.listener = object : BaseListener {
            override fun call() {
                isResultShow(false)
            }
        }
        btn_calculate.setOnClickListener {
            if (et_salary.text.isEmpty()) {
                showToast("还没有输入金额")
                return@setOnClickListener
            }
            calculateYearTax(et_salary.text.toString())
        }
        btn_calculate_original.setOnClickListener {
            if (et_salary.text.isEmpty()) {
                showToast("还没有输入金额")
                return@setOnClickListener
            }
            calculateYearOriginal(et_salary.text.toString())
        }
    }

    private fun calculateYearTax(s: String) {
        val result = calculateTax(s)
        tv_result.text = subtract(s, result)
        val rate = calculateYearTaxRate(s)

        tv_calculation_formula.text = "$s（税前年终奖）* $rate（税率） - ${calculateQuickDeduction(s)}（速算扣除数） = $result（个税）"
        isResultShow(true)
    }

    private fun calculateYearOriginal(s: String) {
        val pos = calculateOriginalSalaryPosition(s) ?: return
        val rate = getTaxRateByPosition(pos)
        val deduction = getQuickDeductionByPosition(pos)

        val result = div(subtract(s, deduction), subtract("1", taxRateList[pos]), 2)
        tv_result.text = result
        tv_calculation_formula.text = "（$s（税后所得）- $deduction（速算扣除数）） ÷ (1 - $rate（税率）)= $result（税前年终奖）"
        isResultShow(true)
    }

    private fun isResultShow(show: Boolean) {
        val vis = if (show) View.VISIBLE else View.GONE
        card.visibility = vis
        tv_yt_02.visibility = vis
        tv_calculation_formula.visibility = vis
    }
}