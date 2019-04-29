package com.flayone.taxcc.taxcomparecalculate

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.view.ViewGroup
import com.flayone.taxcc.taxcomparecalculate.base.BaseActivity
import com.flayone.taxcc.taxcomparecalculate.base.BaseApp
import com.flayone.taxcc.taxcomparecalculate.dialog.CustomParametersDialog
import com.flayone.taxcc.taxcomparecalculate.items.YearHistoryItem
import com.flayone.taxcc.taxcomparecalculate.items.YearResultItem
import com.flayone.taxcc.taxcomparecalculate.utils.*
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_year_calculate.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class YearCalculateActivity : BaseActivity() {

    private var salaryVal = "0" //输入的税前薪水
    private var welfareVal = "0" //五险一金对应的值
    private var plusNumber = "0" //新个税附加扣除数
    private var cumulativeCalculateVal = "0" //当前年累计预扣预缴税额
    private var cumulativeTax = "0" //当前年累计个税
    private var salaryList = arrayListOf<String>() //12个月的月薪基数集合
    private val resultData = ResultListModel()
    //记录当前输入的参数,可自定义值来完善计算结果
    private var inputData = mutableListOf<BaseCalculateModel>()
    private var historyList = YearHistoryListModel()
    private var mHistoryModel = YearHistoryModel()
//    private val isCustomParameters = false //是否用户自定义了各个月份的参数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_year_calculate)
        ripple(calculate, 90f)
    }

    override fun initView() {
        super.initView()
        setTitle("新个税计算器(2019.1.1后)")
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
                salaryVal = s
                if (salaryVal.isEmpty()) {
                    initLayout()
                } else {
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
        var mLeft = getScreenW() - dp2px(76f)
        var mTop = getScreenHWithOutBar() - dp2px(88f)

        val lp = tv_pop_section.layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(mLeft, mTop, 0, 0)
        tv_pop_section.layoutParams = lp
        //年终奖
        tv_pop_section.setOnTouchListener(OnDragListener(true, object : OnDraggableClickListener {
            override fun onDragged(v: View, left: Int, top: Int) {
                mLeft = left
                mTop = top
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onClick(v: View) {
                startActDrop(tv_pop_bg, mLeft + (dragWidth / 2), mTop + (dragHeight / 2), dragWidth.toFloat(), getScreenH().toFloat()) {
//                    showToast("年终奖")
                    startAnimAct(YearTaxActivity())
                }
            }

        }))
    }

    //页面显示相关的初始化
    private fun initLayout() {
        til_welfare.visibility = View.GONE
        til_expend.visibility = View.GONE
        calculate_tips.visibility = View.GONE
        et_expend.setText("")
        et_welfare.setText("")
        calculate_tips.text = ""
        initHistory()
    }

    //历史数据初始化
    private fun initHistory() {
        try {
            historyList = getObject(this, LOCAL_Data, HISTORY_TAG_YEAR) as YearHistoryListModel
            Logger.json(historyList.toJSON())
            list_result.adapter = YearHistoryItem(historyList.list, object : BasePositionListener {
                override fun onClick(i: Int) {
                    val item = historyList.list[i]
                    et_salary.setText(item.inputSalary)
                    et_welfare.setText(item.inputWelfare)
                    et_expend.setText(item.inputExpend)
                    inputData = item.hisList
                    calculateTax()
                }
            }, object : BasePositionListener {
                override fun onClick(i: Int) {//单个删除
                    historyList.list.removeAt(i)
                    list_result.adapter.notifyDataSetChanged()
                    saveObject(this@YearCalculateActivity, LOCAL_Data, HISTORY_TAG_YEAR, historyList)
                }
            }, object : BaseListener {
                override fun call() {//全部删除
                    historyList.list.clear()
                    list_result.adapter.notifyDataSetChanged()
                    saveObject(BaseApp.instance, LOCAL_Data, HISTORY_TAG_YEAR, historyList)
                }
            })
            list_result.adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            println(e)
        }
    }

    //这里是普通计算时候装载12个月的输入参数初始数据为顶部部输入值，所以如果是从历史列表来的不走这一步
    private fun preCalculate() {
        inputData.clear()
        for (i in 0 until 12) {
            val historyModel = BaseCalculateModel()
            //输入参数赋值
            historyModel.run {
                baseSalary = salaryVal
                expend = plusNumber
                taxThreshold = threshold
                welfare = welfareVal
            }
            inputData.add(historyModel)
        }
    }

    // 计算税额并列出所有月份的结果
    private fun calculateTax() {
        mHistoryModel = YearHistoryModel()
        calculate_tips.visibility = View.VISIBLE
        mHistoryModel.run {
            inputSalary = salaryVal
            inputExpend = plusNumber
            inputWelfare = welfareVal
        }
        val baseInputData = BaseCalculateModel()
        //当前页面输入的基本参数，用来对自定义参数不需要同步的数据初始化
        baseInputData.run {
            baseSalary = salaryVal
            welfare = welfareVal
            expend = plusNumber
        }
        resultData.list.clear()
        for (i in 0 until 12) {
            resultData.list.add(CalculateResult())
        }
        for (i in 0 until 12) {
            val list = resultData.list[i]
            val inputItem = inputData[i]
//            calculateVal是计算当月预扣预缴的总额，最终用来计算个人所得税, 低收入者该值为负，手动置0
            list.calculateVal = subtract(inputItem.baseSalary, inputItem.welfare, inputItem.taxThreshold, inputItem.expend)
            if (list.calculateVal.toDouble() < 0) {
                list.calculateVal = "0"
            }
            var lastCumulativeCalculateVal = "0"
            var lastCumulativeTax = "0"
            if (i > 0) {
                lastCumulativeCalculateVal = resultData.list[i - 1].cumulativeCalculateVal
                lastCumulativeTax = resultData.list[i - 1].cumulativetax
            }
            cumulativeCalculateVal = add(list.calculateVal, lastCumulativeCalculateVal)
            list.cumulativeCalculateVal = cumulativeCalculateVal

            cumulativeTax = calculateTax(cumulativeCalculateVal)
            list.cumulativetax = cumulativeTax
            list.tax = subtract(list.cumulativetax, lastCumulativeTax)
            //税后薪资计算
            list.afterTaxSalary = subtract(inputItem.baseSalary, inputItem.welfare, list.tax)
            list.beforeTaxSalary = salaryVal
            list.inputSalary = inputItem.baseSalary
        }

        list_result.adapter = YearResultItem(resultData.list, object : BasePositionListener {
            override fun onClick(i: Int) {
                //展示自定义参数对话框，回返一些自定义的参数，赋值inputData 重新计算薪资
                CustomParametersDialog(this@YearCalculateActivity, "自定义${i + 1}月收入", inputData[i]) {

                    //单击确定后将自定义的数据赋值，重新计算税后
                    if (needHistorySynchronized) {
                        inputData[i] = this
                    } else {
                        inputData[i] = baseInputData
                    }
                    calculateTax()
                }.show()

            }
        })
//        将此次计算归入历史记录
        mHistoryModel.hisList = inputData
        mHistoryModel.yearSalary = calculateYearSalaryBeforeTax()
        mHistoryModel.yearAfterTax = calculateYearSalaryAfterTax()
        mHistoryModel.yearTax = cumulativeTax
        calculate_tips.text = "年税前:${shortYearMoney(mHistoryModel.yearSalary)}, 税:${shortMoney(mHistoryModel.yearTax)}, 到手:${shortYearMoney(mHistoryModel.yearAfterTax)}"

        val hisCount = historyList.list.size
        //判断是否同一条记录，是的话需要将旧的替换为新的记录，因为在当前页自定义月工资时mHistoryModel指向的还是同一个对象，但mHistoryModel.hisList数据是变化了的，需要更新数据，不是同一条记录则执行保存操作
        var isTheSameRequest = false
        if (hisCount > 0) {
            for (i in 0 until hisCount) {
                if (mHistoryModel == historyList.list[i]) {
                    isTheSameRequest = true
//                    historyList.list[i] = mHistoryModel

                    //相同记录重置顶,保证时间顺序
                    historyList.list.removeAt(i)
                    historyList.list.add(mHistoryModel)
                }
            }
        }
        if (!isTheSameRequest) {
            if (hisCount > hisLimit) {
                historyList.list.removeAt(0)
            }
            historyList.list.add(mHistoryModel)
        }
        saveObject(this, LOCAL_Data, HISTORY_TAG_YEAR, historyList)
        Logger.json(historyList.toJSON())
    }



    private fun calculateYearSalaryBeforeTax(): String {
        var result = ""
        inputData.forEach {
            result = add(result, it.baseSalary)
        }
        return result
    }

    private fun calculateYearSalaryAfterTax(): String {
        var result = ""
        resultData.list.forEach {
            result = add(result, it.afterTaxSalary)
        }
        return result
    }


    var dragHeight = 0
    var dragWidth = 0

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            dragHeight = tv_pop_section.height
            dragWidth = tv_pop_section.width
        }
    }
}