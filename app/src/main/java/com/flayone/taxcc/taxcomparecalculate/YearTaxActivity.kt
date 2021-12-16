package com.flayone.taxcc.taxcomparecalculate

import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.View
import android.widget.AdapterView
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import com.flayone.taxcc.taxcomparecalculate.dialog.RateHelpDialog
import com.flayone.taxcc.taxcomparecalculate.utils.*
import kotlinx.android.synthetic.main.activity_year_tax.*

/**
 * Created by liyayu on 2019/4/23.
 * 计算年终奖的个人所得税
 */
class YearTaxActivity : BaseActivity() {

    private val duration = 550L
    private val mainColor = R.color.y_bg
    private var rateSelectPos = 0

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

        v_ayt_01.setOnClickListener {
            RateHelpDialog(this).show()
        }

        acs_rate.setSelection(0)
        acs_rate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                rateSelectPos = position

            }

        }
    }

    private fun calculateYearTax(s: String) {

        val is2021 = rateSelectPos == 0//旧版计算方式
        val result = if (is2021) calculateTax(s) else {
            multiply(s, taxRateList[rateSelectPos - 1], 2)
        }
        tv_result.text = subtract(s, result)
        val rate = calculateYearTaxRate(s)

        val tip = if (is2021) "$s（税前年终奖）* $rate（税率） - ${calculateQuickDeduction(s)}（速算扣除数） = $result（个税）" else{
            "$s（税前年终奖）* ${taxRateList[rateSelectPos - 1]}（税率） = $result（个税）"
        }
        tv_calculation_formula.text =  tip
        isResultShow(true)
    }

    private fun calculateYearOriginal(s: String) {
        val is2021 = rateSelectPos == 0//旧版计算方式

        val pos = calculateOriginalSalaryPosition(s) ?: return
        val rate = getTaxRateByPosition(pos)
        val deduction = getQuickDeductionByPosition(pos)

        val rateInf = if (is2021) taxRateList[pos] else taxRateList[rateSelectPos - 1]
        val result = if (is2021) div(subtract(s, deduction), subtract("1", rateInf), 2)else{
            div( s, subtract("1", rateInf), 2)
        }
        tv_result.text = result

        val tip = if (is2021)"（$s（税后所得）- $deduction（速算扣除数）） ÷ (1 - $rate（税率）)= $result（税前年终奖）" else{
            "$s（税后所得） ÷ (1 - ${taxRateList[rateSelectPos - 1]}（税率）)= $result（税前年终奖）"
        }

        tv_calculation_formula.text = tip
        isResultShow(true)
    }

    private fun isResultShow(show: Boolean) {
        val vis = if (show) View.VISIBLE else View.GONE
        card.visibility = vis
        tv_yt_02.visibility = vis
        tv_calculation_formula.visibility = vis
    }
}