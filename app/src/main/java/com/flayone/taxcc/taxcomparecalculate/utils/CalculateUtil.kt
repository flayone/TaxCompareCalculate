package com.flayone.taxcc.taxcomparecalculate.utils

import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import java.math.BigDecimal


/**
 * Created by liyayu on 2018/6/20.
 * 金额计算相关
 */
//设置金额输入小数点后2位有效
fun keepEditTwoPoint(editText: EditText) {
    editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
    editText.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
        // 在无内容或者现有内容全选，输入.时自动添加0
        if (source == "." && (dest.toString().isEmpty() || dstart == 0 && dend == dest.length)) {
            return@InputFilter "0."
        }
        // 获取新的string
        val newString = dest.toString().substring(0, dstart) + source.subSequence(start, end) + dest.toString().substring(dend)
        // 如果新的string超过1个.就认为不合法
        if (newString.indexOf(".") != newString.lastIndexOf(".")) {
            return@InputFilter ""
        }
        // 如果新的string有.且小数超过2位就认为不合法
        if (newString.contains(".")) {
            val index = newString.indexOf(".")
            if (newString.length - index - 1 > 2) {
                return@InputFilter ""
            }
        }
        //相当于maxLength
//        if (newString.length > 10) {
//            ""
//        } else null
        return@InputFilter null
    })
}

// + 高精度加法，避免出现0.2+0.1二进制转换失准情况
fun add(v1: String, v2: String): String {
    return try {
        val b1 = BigDecimal(if (v1.isEmpty()) "0" else v1)
        val b2 = BigDecimal(if (v2.isEmpty()) "0" else v2)
        //            return b1.add(b2).toString();
        b1.add(b2).stripTrailingZeros().toPlainString()//去掉无用的小数点以及末尾的0
    } catch (e: Exception) {
        e.printStackTrace()
        "0"
    }

}

// + 多个相加
fun add(vararg v1: String): String {
    return try {
        var a1 = BigDecimal("0")
        var b1: BigDecimal
        for (s in v1) {
            b1 = BigDecimal(if (s.isEmpty()) "0" else s)
            a1 = a1.add(b1)
        }
        a1.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        "0"
    }
}

fun subtract(vararg ss: String): String {
    return try {
        var a1 = BigDecimal(if (ss[0].isEmpty()) "0" else ss[0])
        var b1: BigDecimal
        for (i in 1 until ss.size) {
            b1 = BigDecimal(if (ss[i].isEmpty()) "0" else ss[i])
            a1 = a1.subtract(b1)
        }
        a1.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        "0"
    }
}

// - 减法
fun subtract(v1: String, v2: String): String {
    return try {
        val b1 = BigDecimal(if (v1.isEmpty()) "0" else v1)
        val b2 = BigDecimal(if (v2.isEmpty()) "0" else v2)
        b1.subtract(b2).stripTrailingZeros().toPlainString()//去掉无用的小数点以及末尾的0
    } catch (e: Exception) {
        e.printStackTrace()
        "0"
    }

}

/**
 * -
 *
 * @param v1
 * @param v2
 * @param scale 保留小数位数，采用四舍五入方式
 * @return
 */
fun subtract(v1: String, v2: String, scale: Int): String {
    return try {
        val b1 = BigDecimal(if (v1.isEmpty()) "0" else v1)
        val b2 = BigDecimal(if (v2.isEmpty()) "0" else v2)
        b1.subtract(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        "0"
    }

}

// *
fun multiply(v1: String, v2: String): String {
    return try {
        val b1 = BigDecimal(if (v1.isEmpty()) "0" else v1)
        val b2 = BigDecimal(if (v2.isEmpty()) "0" else v2)
        b1.multiply(b2).toString()
    } catch (e: Exception) {
        e.printStackTrace()

        "0"
    }

}

/**
 * @param v1
 * @param v2
 * @param scale 保留小数位数，采用四舍五入方式
 * @return
 */
fun multiply(v1: String, v2: String, scale: Int): String {
    return try {
        val b1 = BigDecimal(if (v1.isEmpty()) "0" else v1)
        val b2 = BigDecimal(if (v2.isEmpty()) "0" else v2)
        b1.multiply(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString()
    } catch (e: Exception) {
        e.printStackTrace()

        "0"
    }

}

// 比大小，-1：小于 0：等于 1：大于
fun compareTo(v1: String, v2: String): Int {
    return try {
        val b1 = BigDecimal(if (v1.isEmpty()) "0" else v1)
        val b2 = BigDecimal(if (v2.isEmpty()) "0" else v2)
        b1.compareTo(b2)
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }

}

/**
 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
 *
 * @param v1    被除数
 * @param v2    除数
 * @param scale 表示表示需要精确到小数点以后几位。
 * @return 两个参数的商
 */

fun div(v1: String, v2: String, scale: Int): String {
    if (scale < 0) {
        throw IllegalArgumentException(
                "The scale must be a positive integer or zero")
    }
    return try {
        val b1 = BigDecimal(v1)
        val b2 = BigDecimal(v2)
        b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        "0"
    }
}

fun shortMoney(ori: String): String {
    if (ori.toDouble() <= 999.99) {
        return ori
    }
    if (ori.toDouble() <= 999999.99) {
        return "${div(ori, "1000", 1)}K"
    }
    return "${div(ori, "1000000", 1)}M"
}

fun shortYearMoney(ori: String): String {
    if (ori.toDouble() <= 9999.99) {
        return ori
    }
    if (ori.toDouble() <= 999999.99) {
        return "${div(ori, "10000", 1)}W"
    }
    return "${div(ori, "1000000", 1)}M"
}

fun calculateYearTaxPosition(s: String): Int? = when (s.toDouble()) {
    in yearLevelList[0] until yearLevelList[1] -> 0
    in yearLevelList[1] until yearLevelList[2] -> 1
    in yearLevelList[2] until yearLevelList[3] -> 2
    in yearLevelList[3] until yearLevelList[4] -> 3
    in yearLevelList[4] until yearLevelList[5] -> 4
    in yearLevelList[5] until yearLevelList[6] -> 5
    in yearLevelList[6] until Int.MAX_VALUE -> 6
    else -> {
        null
    }
}

fun calculateYearTaxRate(s: String): String {
    val pos = calculateYearTaxPosition(s)
    return if (pos != null)
        "${multiply(taxRateList[pos], "100")}%"
    else ""
}


private val quickDeductionList = getQuickDeductionList(yearLevelList, taxRateList)

// 根据当月年化累计预扣预缴税额来计算个税数：
fun calculateTax(s: String): String = when (s.toDouble()) {
    in yearLevelList[0] until yearLevelList[1] -> calculateTax(s, taxRateList[0], quickDeductionList[0])
    in yearLevelList[1] until yearLevelList[2] -> calculateTax(s, taxRateList[1], quickDeductionList[1])
    in yearLevelList[2] until yearLevelList[3] -> calculateTax(s, taxRateList[2], quickDeductionList[2])
    in yearLevelList[3] until yearLevelList[4] -> calculateTax(s, taxRateList[3], quickDeductionList[3])
    in yearLevelList[4] until yearLevelList[5] -> calculateTax(s, taxRateList[4], quickDeductionList[4])
    in yearLevelList[5] until yearLevelList[6] -> calculateTax(s, taxRateList[5], quickDeductionList[5])
    in yearLevelList[6] until Int.MAX_VALUE -> calculateTax(s, taxRateList[6], quickDeductionList[6])
    else -> {
        "0"
    }
}

//根据当月年化累计预扣预缴税额、税率区间和速算扣除数来计算 个税金额
fun calculateTax(cumulativeCalculateVal: String, taxRate: String, quickDeduction: Int) = subtract(multiply(cumulativeCalculateVal, taxRate, 2), quickDeduction.toString())

fun calculateQuickDeduction(s: String): String {
    val pos = calculateYearTaxPosition(s)
    return if (pos != null)
        quickDeductionList[pos].toString()
    else ""
}

//s代表输入的税后金额
fun calculateOriginalSalaryPosition(s: String): Int? {
    for (i in 1 until yearLevelList.size) {
        //边界值最大，用来判断当前输入值的所在扣税区间
        val maxEdgeValue = if (i < yearLevelList.size) {
            subtract(yearLevelList[i].toString(), calculateTax(yearLevelList[i].toString()))
        } else {
            Int.MAX_VALUE.toString()
        }
        val isInside = compareTo(s, maxEdgeValue) <= 0 //当输入的值小于等于区间最大值时
        if (isInside) {
            return i - 1
        }
    }
    return null
}

//根据区间位置获取税率
fun getTaxRateByPosition(pos: Int?): String = if (pos == null) "" else {
    "${multiply(taxRateList[pos], "100")}%"
}

//根据区间位置获取速算扣除数
fun getQuickDeductionByPosition(pos: Int?) = if (pos == null) "" else {
    quickDeductionList[pos].toString()
}
