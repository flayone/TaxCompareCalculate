package com.flayone.taxcc.taxcomparecalculate.items

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.flayone.taxcc.taxcomparecalculate.R
import com.flayone.taxcc.taxcomparecalculate.utils.*
import kotlinx.android.synthetic.main.item_history.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class HistoryItem(var data: List<CalculateHistoryModel>, private val listener: BasePositionListener) : RecyclerView.Adapter<MyViewHolder>() {
    private lateinit var con: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        con = parent.context
        return MyViewHolder(View.inflate(parent.context, R.layout.item_history, null))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        with(holder?.itemView!!) {
            if (position == 0) {
                btn_clear.visibility = View.VISIBLE
                title.visibility = View.VISIBLE
                btn_clear.onClick {
                    saveObject(con, LOCAL_Data, HISTORY_TAG_A, HistoryListModel())
                    data = mutableListOf()
                    notifyDataSetChanged()
                }
            } else {
                btn_clear.visibility = View.GONE
                title.visibility = View.GONE
            }
            val pos = data.size - (position+1)
            tx_salary.text = shortMoney(data[pos].afterTex)
            tx_detail.text = "税前:${data[pos].baseSalary}，险金:${data[pos].welfare}，税:${data[pos].tax}，附加数:${data[pos].expend}"
            cons_parent.onClick {
                listener.onClick(pos)
            }
        }
    }

}