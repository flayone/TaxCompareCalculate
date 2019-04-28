package com.flayone.taxcc.taxcomparecalculate.items

import android.view.View
import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.dialog.showAlertDialog
import com.flayone.taxcc.taxcomparecalculate.utils.*
import kotlinx.android.synthetic.main.item_history.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLongClick

class YearHistoryItem(override var list: List<Any>, private val listener: BasePositionListener,private val delListener: BasePositionListener,
private val delAllListener: BaseListener) : BaseRecycleListAdapter(list, R.layout.item_history) {
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = with(holder.itemView) {
        val data = list as List<YearHistoryModel>

        if (position == 0) {
            title.text = "历史记录（年度）"
            btn_clear.visibility = View.VISIBLE
            title.visibility = View.VISIBLE
            btn_clear.setOnClickListener {
                showAlertDialog(mContext,"确认要清除所有记录吗？",{
                    delAllListener.call()
                })
            }
        } else {
            btn_clear.visibility = View.GONE
            title.visibility = View.GONE
        }
        val pos = data.size - (position + 1)
        tx_salary.text = shortYearMoney(data[pos].yearAfterTax)
        tx_detail.text = "税前:${shortYearMoney(data[pos].yearSalary)}(月均：${shortMoney(div(data[pos].yearSalary, "12", 2))})，税:${shortMoney(data[pos].yearTax)}，月均收入:${shortMoney(div(data[pos].yearAfterTax, "12", 2))}"
        cons_parent.onClick {
            listener.onClick(pos)
        }
        cons_parent.onLongClick {
            showAlertDialog(mContext,"确认要删除此条(${shortYearMoney(data[pos].yearAfterTax)})记录吗？",{
                delListener.onClick(pos)
            })
        }
    }
}