package com.flayone.taxcc.taxcomparecalculate.items

import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.base.BaseApp
import com.flayone.taxcc.taxcomparecalculate.utils.*
import kotlinx.android.synthetic.main.item_result.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class YearResultItem(override var list: List<Any>, private val listener: BasePositionListener) : BaseRecycleListAdapter(list, R.layout.item_result) {
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = with(holder.itemView) {
        val data = list[position] as CalculateResult

        tx_salary.text = data.afterTaxSalary
        tx_calculate.text = "计税金额:" + data.calculateVal
        tx_tax.text = "个税:" + data.tax
        tx_total_tax.text = "个税累计:" + data.cumulativetax
        tx_total_calculate.text = "计税金额累计:" + data.cumulativeCalculateVal
        tx_tax_rate.text = "税率: ${calculateYearTaxRate(data.cumulativeCalculateVal)}"

        tx_month.text = getMonthPicUniCode(position)

        tx_edit.onClick { listener.onClick(position) }
        //自定义数值
        if (data.beforeTaxSalary != data.inputSalary) {
            tx_edit.text = "自定义\n(${shortMoney(data.inputSalary)})"
        }
    }

    private fun getMonthPicUniCode(p: Int): String {
        val res = when (p) {
            0 -> R.string.January
            1 -> R.string.February
            2 -> R.string.March
            3 -> R.string.April
            4 -> R.string.May
            5 -> R.string.June
            6 -> R.string.July
            7 -> R.string.August
            8 -> R.string.September
            9 -> R.string.October
            10 -> R.string.November
            11 -> R.string.December
            else -> {
                0
            }
        }
        val mo = BaseApp.instance.getString(R.string.month)
        val num = BaseApp.instance.getString(res)
        return if (res == 0) {
            ""
        } else {
            "$num $mo"
        }
    }
}