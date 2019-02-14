package com.flayone.taxcc.taxcomparecalculate

import android.os.Bundle
import android.view.View
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import com.flayone.taxcc.taxcomparecalculate.items.YearHistoryItem
import com.flayone.taxcc.taxcomparecalculate.items.YearResultItem
import com.flayone.taxcc.taxcomparecalculate.utils.*
import kotlinx.android.synthetic.main.activity_year_calculate.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class YearCalculateActivity : BaseActivity() {

    private val yearLevelList = arrayListOf(0, 36000, 144000, 300000, 420000, 660000, 960000)
    private val taxRateList = arrayListOf("0.03", "0.10", "0.20", "0.25", "0.30", "0.35", "0.45")

    private val threshold = "5000" //起征点
    private var salaryVal = "" //输入的税前薪水
    private var welfareVal = "" //五险一金对应的值
    private var plusNumber = "0" //新个税附加扣除数
    private var cumulativeCalculateVal = "0" //当月累计预扣预缴税额
    private var cumulativetax = "" //年累计个税
    private var salaryList = arrayListOf<String>() //12个月的月薪基数集合
    private val resultData = ResultListModel()
    //记录当前输入的参数,可自定义值来完善计算结果
    private var inputData = mutableListOf<CalculateHistoryModel>()
    private val quickDeductionList = getQuickDeductionList(yearLevelList, taxRateList)
    private var historyList = YearHistoryListModel()
    private val mHistoryModel = YearHistoryModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_year_calculate)
        setTitle("个税计算器（年累计算法）")
        ripple(calculate, 90f)
        calculate_tips.text = "单击列表项可修改该月收入情况"
    }

    override fun initView() {
        super.initView()
        keepEditTwoPoint(et_salary)
        keepEditTwoPoint(et_welfare)
        keepEditTwoPoint(et_expend)
        list_result.isNestedScrollingEnabled = false
        list_result.layoutManager = initRecycleLayoutManger(this)

        initLayout()
        salaryList = arrayListOf()
        resultData.list = arrayListOf()
        et_salary.addTextChangedListener(MyTextWatcher(object : BaseEnsureListener {
            override fun ensure(s: String) {
                if (s.isEmpty()) {
                    initLayout()
                } else {
                    salaryVal = s
                    welfareVal = calculateWelfare(salaryVal)
                    //初始化每个月的薪资
                    for (i in 0 until 12) {
                        salaryList.add(salaryVal)
                    }

                    et_welfare.setText(welfareVal)
                    til_welfare.visibility = View.VISIBLE
                    til_expend.visibility = View.VISIBLE
                }
            }
        }))
        et_welfare.addTextChangedListener(MyTextWatcher(object : BaseEnsureListener {
            override fun ensure(s: String) {
                welfareVal = s
            }
        }))
        et_expend.addTextChangedListener(MyTextWatcher(object : BaseEnsureListener {
            override fun ensure(s: String) {
                plusNumber = s
            }
        }))
        calculate.onClick {
            val calculateVal = subtract(salaryVal, welfareVal)
            if (salaryVal.isEmpty()) {
                showToast("还没输入工资呢~")
                return@onClick
            }
            if (calculateVal.toFloat() <= 0) {
                showToast("这么些钱还不够吃饭呢，真可怜，抱抱你ε==(づ′▽`)づ")
                return@onClick
            }
            if (calculateVal.toFloat() >= 50000) {
                showToast("(*@ο@*) 哇  大佬")
            }

            preCalculate()
            calculateTax()
        }

    }

    //页面显示相关的初始化
    private fun initLayout() {
        til_welfare.visibility = View.GONE
        til_expend.visibility = View.GONE
        calculate_tips.visibility = View.GONE
        initHistory()
    }

    //历史数据初始化
    private fun initHistory() {
        try {
            historyList = getObject(this, LOCAL_Data, HISTORY_TAG_YEAR) as YearHistoryListModel

            list_result.adapter = YearHistoryItem(historyList.list, object : BasePositionListener {
                override fun onClick(i: Int) {
                    val item = historyList.list[i]
                    et_salary.setText(item.inputSalary)
                    et_welfare.setText(item.inputWelfare)
                    et_expend.setText(item.inputExpend)
                    inputData = item.hisList
                    calculateTax()
                }
            })

            list_result.visibility = View.VISIBLE
        } catch (e: Exception) {
            println(e)
        }
    }

    //准备每一个月的基础数据，方便后续可定制计划
    private fun preCalculate() {
        inputData.clear()
        resultData.list.clear()
        for (i in 0 until 12) {
            val historyModel = CalculateHistoryModel()
            //输入参数赋值
            historyModel.run {
                baseSalary = salaryVal
                expend = plusNumber
                taxThreshold = threshold
                welfare = welfareVal
            }
            inputData.add(historyModel)
            resultData.list.add(CalculateResult())
            mHistoryModel.run {
                inputSalary = salaryVal
                inputExpend = plusNumber
                inputWelfare = welfareVal
            }
        }
    }

    // 计算税额并列出所有月份的结果
    private fun calculateTax() {
        calculate_tips.visibility = View.VISIBLE

        for (i in 0 until 12) {
            val list = resultData.list[i]
            val inputItem = inputData[i]
//            calculateVal是计算当月预扣预缴的总额，最终用来计算个人所得税
            list.calculateVal = subtract(inputItem.baseSalary, inputItem.welfare, inputItem.taxThreshold, inputItem.expend)
            var lastCumulativeCalculateVal = "0"
            var lastcumulativetax = "0"
            if (i > 0) {
                lastCumulativeCalculateVal = resultData.list[i - 1].cumulativeCalculateVal
                lastcumulativetax = resultData.list[i - 1].cumulativetax
            }
            cumulativeCalculateVal = add(list.calculateVal, lastCumulativeCalculateVal)
            list.cumulativeCalculateVal = cumulativeCalculateVal

            cumulativetax = calculateTax(cumulativeCalculateVal)
            list.cumulativetax = cumulativetax
            list.tax = subtract(list.cumulativetax, lastcumulativetax)
            //税后薪资计算
            list.afterTaxSalary = subtract(salaryVal, inputItem.welfare, list.tax)
        }
        d("resultData = ${resultData.list.size}")

        list_result.adapter = YearResultItem(resultData.list)

//        将此次计算归入历史记录
        mHistoryModel.hisList = inputData
        historyList
    }

    // 根据当月年化累计预扣预缴税额来计算个税数：
    private fun calculateTax(s: String): String = when (s.toDouble()) {
        in yearLevelList[0] until yearLevelList[1] -> calculateTax(s, taxRateList[0], quickDeductionList[0])
        in yearLevelList[1] until yearLevelList[2] -> calculateTax(s, taxRateList[1], quickDeductionList[1])
        in yearLevelList[2] until yearLevelList[3] -> calculateTax(s, taxRateList[2], quickDeductionList[2])
        in yearLevelList[3] until yearLevelList[4] -> calculateTax(s, taxRateList[3], quickDeductionList[3])
        in yearLevelList[4] until yearLevelList[5] -> calculateTax(s, taxRateList[4], quickDeductionList[4])
        in yearLevelList[5] until yearLevelList[6] -> calculateTax(s, taxRateList[5], quickDeductionList[5])
        in yearLevelList[6] until Int.MAX_VALUE -> calculateTax(s, taxRateList[6], quickDeductionList[6])
        else -> {
            ""
        }
    }

    //根据当月年化累计预扣预缴税额、税率区间和速算扣除数来计算 个税金额
    private fun calculateTax(cumulativeCalculateVal: String, taxRate: String, quickDeduction: Int) = subtract(multiply(cumulativeCalculateVal, taxRate, 2), quickDeduction.toString())

}