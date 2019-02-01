package com.flayone.taxcc.taxcomparecalculate.items

import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.utils.BaseRecycleListAdapter
import com.flayone.taxcc.taxcomparecalculate.utils.CalculateResult
import com.flayone.taxcc.taxcomparecalculate.utils.MyViewHolder
import kotlinx.android.synthetic.main.item_result.view.*

class YearResultItem(override var list: List<Any>) : BaseRecycleListAdapter(list, R.layout.item_result) {
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = with(holder.itemView) {
        val data = list[position] as CalculateResult

        tx_salary.text =data.afterTaxSalary
        tx_calculate.text ="计税金额："+data.calculateVal
        tx_tax.text =  "税："+data.tax
        tx_total_tax.text = "累计："+data.cumulativetax
        tx_total_calculate.text = data.cumulativeCalculateVal
    }
}