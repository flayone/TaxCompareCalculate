package com.flayone.taxcc.taxcomparecalculate.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable


open class BaseModel : Serializable, JSONConvertable
class CalculateHistoryModel : BaseModel() {
    var baseSalary = "0" //税前工资
    var welfare = "0"  //五险一金
    var expend = "0"  //附加扣除数
    var afterTex = "0"  //税后
    var tax = "0"  //个税
    var taxThreshold = "0"  //自定义起征数

    override fun equals(other: Any?): Boolean {
        if (this === other) return true   //若指向同一个对象，直接返回true
        val flag = other is CalculateHistoryModel    //判断obj是否属于User这个类
        return if (flag) {
            val model = other as CalculateHistoryModel
            //如果输入参数都相同代表是同一条数据
            model.expend == this.expend && model.welfare == this.welfare && model.baseSalary == this.baseSalary
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = baseSalary.hashCode()
        result = 31 * result + welfare.hashCode()
        result = 31 * result + expend.hashCode()
        result = 31 * result + afterTex.hashCode()
        result = 31 * result + tax.hashCode()
        result = 31 * result + taxThreshold.hashCode()
        return result
    }
}

class HistoryListModel : BaseModel() {
    var list = mutableListOf<CalculateHistoryModel>()
}

class ResultListModel : BaseModel() {
    var list = mutableListOf<CalculateResult>()
}

//年累计计算历史记录用
class YearHistorySearchModel : BaseModel() {
    var baseSalary = "0" //本月税前工资、后续可定制
    var nowThreshold = "0" //本月起征额、后续可定制
    var nowWelfareVal = "0" //本月险金等税前扣除数、后续可定制
    var nowPlusNumber = "0" //本月附加扣除数、后续可定制
}

//年累计计算结果model
class CalculateResult : BaseModel() {
    var afterTaxSalary = "0" //税后工资
    var calculateVal = "0" //当月预扣预缴税额
    var cumulativeCalculateVal = "" //本月累计预扣预缴税额
    var tax = "0" //个税
    var cumulativetax = "" //年累计个税
}

interface JSONConvertable {
    fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T : JSONConvertable> String.toObject(): T = Gson().fromJson(this, T::class.java)

inline fun <reified T : JSONConvertable> String.toList(): MutableList<T> = Gson().fromJson(this, object : TypeToken<List<T>>() {}.type)

