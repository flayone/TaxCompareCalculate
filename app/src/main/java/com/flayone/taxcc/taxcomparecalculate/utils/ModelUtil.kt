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

//月度历史记录列表
class HistoryListModel : BaseModel() {
    var list = mutableListOf<CalculateHistoryModel>()
}

//年度计算结果列表
class ResultListModel : BaseModel() {
    var list = mutableListOf<CalculateResult>()
}

//年度历史记录列表
class YearHistoryListModel: BaseModel() {
    var list = mutableListOf<YearHistoryModel>()
}

class YearHistoryModel : BaseModel() {
    //历史记录列表展示用
    var yearSalary = "0" //年度税前工资、
    var yearWelfare = "0" //年度险金福利
    var yearExpend = "0" //年度附加扣除
    var yearAfterTax = "0" //年度税后
    var yearTax = "0" //年度个税

    //记录输入参数
    var inputSalary = "0" //税前工资
    var inputWelfare = "0"  //五险一金
    var inputExpend = "0"  //附加扣除数

    //    十二个月的详细数据，点击历史记录重新计算用
    var hisList = mutableListOf<CalculateHistoryModel>()
}

//年累计计算历史记录用,单月的详细数据
//class YearHistorySearchModel : BaseModel() {
//    var baseSalary = "0" //本月税前工资、后续可定制
//    var nowThreshold = "0" //本月起征额、后续可定制
//    var nowWelfareVal = "0" //本月险金等税前扣除数、后续可定制
//    var nowPlusNumber = "0" //本月附加扣除数、后续可定制
//}

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

