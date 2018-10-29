package com.flayone.taxcc.taxcomparecalculate

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AlertDialog.Builder
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast


open class MainActivity : BaseActivity() {
    var value: Double? = null
    var salaryVal = ""
    var welfareVal = ""
    var calculateVal = ""
    private var taxNew = ""
    private var taxOld = ""
    private var newTaxThreshold = "5000"//新个税起征点
    private var odlTaxThreshold = "3500"//旧个税起征点

    //(以上海为例)公积金下限基数 2190 社保下限基数 4279
    private val minSocialSafety = 4279
    private val maxSocialSafety = 21396
    private val socialSafetyPercent = "0.105"

    private val minPublicMoney = 2190
    private val maxPublicMoney = 19512
    private val publicMoneyPercent = "0.07"

    private val levelListOld = arrayListOf(0, 1500, 4500, 9000, 35000, 55000, 80000)
    private val taxRateListOld = arrayListOf("0.03", "0.10", "0.20", "0.25", "0.30", "0.35", "0.45")

    private val levelListNew = arrayListOf(0, 3000, 12000, 25000, 35000, 55000, 80000)
    private val taxRateListNew = arrayListOf("0.03", "0.10", "0.20", "0.25", "0.30", "0.35", "0.45")


    private val missCount = "0"//修正参数
    private lateinit var builder: Builder
    private lateinit var etThreshold: TextInputEditText
    private lateinit var dialog: AlertDialog
    private lateinit var dialogView: View
    private val calculateTips = "计税金额计算方式：c1 - c2 -c3"
    private var plusNumber = ""
    private var newCalculateVal = ""
    private var isMain = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!getPreference(this, "SETTING", "LOAD_MODE", false)) {
            isMain = true
            setContentView(R.layout.activity_main)
            Coloring.get().setViewRipple(calculate, 90f)
        } else {
            isMain = false
            setContentView(R.layout.activity_main_section_two)
            Coloring.get().setViewRipple(calculate, 5f)
        }
    }

    override fun initView() {
        til_welfare.visibility = View.GONE
        calculate_tips.visibility = View.GONE
        til_expend.visibility = View.GONE
        showResultLayout(false)

        keepEditTwoPoint(et_salary)
        keepEditTwoPoint(et_welfare)
        et_salary.addTextChangedListener(MyTextWatcher(object : BaseEnsureListener {
            override fun ensure(s: String) {
                if (!s.isEmpty()) {
                    value = s.toDoubleOrNull()
                    salaryVal = s
                    welfareVal = calculateWelfare(salaryVal)
                    calculateVal = subtract(salaryVal, welfareVal)

                    et_welfare.setText(welfareVal)
                    til_welfare.visibility = View.VISIBLE
                    til_expend.visibility = View.VISIBLE
                    calculate_tips.visibility = View.VISIBLE
                }
            }
        }))
        et_welfare.addTextChangedListener(MyTextWatcher(object : BaseEnsureListener {
            override fun ensure(s: String) {
                welfareVal = s
                calculateVal = subtract(salaryVal, welfareVal)
            }

        }))
        et_expend.addTextChangedListener(MyTextWatcher(object : BaseEnsureListener {
            override fun ensure(s: String) {
                plusNumber = s
            }
        }))

        calculate.onClick {
            if (salaryVal.isEmpty()) {
                toast("还没输入工资呢~")
                return@onClick
            }
            if (calculateVal.toFloat() <= 0) {
                toast("这么些钱还不够吃饭呢，真可怜，抱抱你ε==(づ′▽`)づ")
                return@onClick
            }
            if (calculateVal.toFloat() >= 50000) {
                toast("(*@ο@*) 哇～ 银才呀")
            }
            calculateAfterTax()
        }

        text1.onClick {
            showDialog(this@MainActivity, "修改起征点",newTaxThreshold, object : BaseEnsureListener {
                override fun ensure(s: String) {
                    newTaxThreshold = s
                    text1.text = "$newTaxThreshold 起征"
                    calculateAfterTax()
                }
            })
        }
        mode_change.onClick {
            setPreferences(this@MainActivity, "SETTING", "LOAD_MODE", !getPreference(this@MainActivity, "SETTING", "LOAD_MODE", false))
            startAct(SectionTwoActivity::class.java)
            finish()
        }
    }

    private fun calculateAfterTax() {
        newCalculateVal = subtract(calculateVal, plusNumber)
        showResultLayout(true)
        taxOld = calculateTax(subtract(calculateVal, odlTaxThreshold).toDouble(), 0)
        taxNew = calculateTax(subtract(newCalculateVal, newTaxThreshold).toDouble(), 1)

        val savePercent = ((100 * subtract(taxOld, taxNew).toDouble()) / taxOld.toDouble()).toInt()
        text5.text = taxNew
        text6.text = taxOld

        real_salary_new.text = subtract(calculateVal, taxNew)
        real_salary_old.text = subtract(calculateVal, taxOld)
        profit.text = ("纳税降幅$savePercent%，可省：￥ " + subtract(taxOld, taxNew))
    }

    private fun showResultLayout(isShow: Boolean) {
        result_hide_bg.visibility = if (!isShow) {
            View.VISIBLE
        } else {
            View.GONE
        }
        calculate_tips.text = calculateTips
        if (isMain){
            text3.text = "个税：(计税金额 $newCalculateVal)"
            text4.text = "个税：(计税金额 $calculateVal)"
        }
    }

    private fun calculateTax(calculateVal: Double, flag: Int): String {
        val levelList = mutableListOf<Int>()//月应纳税所得额（元）的集合
        val taxRateList = mutableListOf<String>()//税率的集合
        val quickDeductionList = mutableListOf<Int>()//速算扣除数的集合

        if (flag == 0) {
            levelList.addAll(levelListOld)
            taxRateList.addAll(taxRateListOld)
        } else {
            levelList.addAll(levelListNew)
            taxRateList.addAll(taxRateListNew)
        }
        //速算扣除数的计算公式是： 本级速算扣除额=上一级最高应纳税所得额×（本级税率-上一级税率）+上一级速算扣除数
        for (i in 1..levelList.size) {
            if (i == 1) {
                quickDeductionList.add(0)
            } else {
                quickDeductionList.add((levelList[i - 1] * (subtract(taxRateList[i - 1], taxRateList[i - 2]).toDouble())).toInt() + quickDeductionList[i - 2])
            }
        }
        //计算收入在不同区间的税额
        return when (calculateVal) {
            in levelList[0] until levelList[1] -> calculateTax(taxRateList[0], quickDeductionList[0], flag)
            in levelList[1] until levelList[2] -> calculateTax(taxRateList[1], quickDeductionList[1], flag)
            in levelList[2] until levelList[3] -> calculateTax(taxRateList[2], quickDeductionList[2], flag)
            in levelList[3] until levelList[4] -> calculateTax(taxRateList[3], quickDeductionList[3], flag)
            in levelList[4] until levelList[5] -> calculateTax(taxRateList[4], quickDeductionList[4], flag)
            in levelList[5] until levelList[6] -> calculateTax(taxRateList[5], quickDeductionList[5], flag)
            in levelList[6] until Int.MAX_VALUE -> calculateTax(taxRateList[6], quickDeductionList[6], flag)
            else -> "0"
        }
    }

    //quickDeduction 速算扣除数,taxRate 税率,flag 0 = 旧，1 =新
    private fun calculateTax(taxRate: String, quickDeduction: Int, flag: Int): String {
        return when (flag) {
            0 -> subtract(multiply(subtract(calculateVal, odlTaxThreshold), taxRate, 2), quickDeduction.toString())
            1 -> subtract(multiply(subtract(newCalculateVal, newTaxThreshold), taxRate, 2), quickDeduction.toString())
            else -> ""
        }
    }

    private fun calculateWelfare(salaryVal: String): String {
        return when (salaryVal.toDouble()) {
            in 0..minPublicMoney -> //公积金下限、社保下限以下。
                add(multiply(minPublicMoney.toString(), publicMoneyPercent, 2), multiply(minSocialSafety.toString(), socialSafetyPercent, 2), missCount)
            in minPublicMoney..minSocialSafety -> //公积金7%、社保下限
                add(multiply(salaryVal, publicMoneyPercent, 2), multiply(minSocialSafety.toString(), socialSafetyPercent, 2), missCount)
            in minSocialSafety..maxPublicMoney -> //公积金7%,社保10.5%
                add(multiply(salaryVal, publicMoneyPercent, 2), multiply(salaryVal, socialSafetyPercent, 2), missCount)
            in maxPublicMoney..maxSocialSafety -> //公积金上限,社保10.5%
                add(multiply(maxPublicMoney.toString(), publicMoneyPercent, 2), multiply(salaryVal, socialSafetyPercent, 2), missCount)
            in maxSocialSafety..Int.MAX_VALUE -> {//公积金上限,社保上限
                add(multiply(maxPublicMoney.toString(), publicMoneyPercent, 2), multiply(maxSocialSafety.toString(), socialSafetyPercent, 2), missCount)
            }
            else -> "0"
        }
    }
}
