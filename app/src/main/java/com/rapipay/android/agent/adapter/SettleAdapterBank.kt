package com.rapipay.android.agent.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rapipay.android.agent.Model.SettlementPozoo
import com.rapipay.android.agent.R
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SettleAdapterBank(private val context: Context,private val mValues: ArrayList<SettlementPozoo>) : RecyclerView.Adapter<SettleAdapterBank.ViewHolder>() {

    private var arraylist: ArrayList<SettlementPozoo>?=null
    constructor(contexts: Context, mValues: ArrayList<SettlementPozoo>, context: Context):this(contexts,mValues){
        this.arraylist = java.util.ArrayList<SettlementPozoo>()
        this.arraylist!!.addAll(mValues)
    }
    class ViewHolder(val views: View) : RecyclerView.ViewHolder(views) {
        var btn_p_bank: TextView? = null
        var btn_name: TextView? = null
        var p_transid: TextView? = null
        var btn_p_amounts: TextView? = null
        var agent_category: TextView? = null
        var serfee: TextView? = null
        var isgct: TextView? = null
        init {
            btn_name = views.findViewById<View>(R.id.numbers) as TextView
            btn_p_amounts = views.findViewById<View>(R.id.names) as TextView
            p_transid = views.findViewById<View>(R.id.limits) as TextView
            btn_p_bank = views.findViewById<View>(R.id.statuss) as TextView
            agent_category = views.findViewById<View>(R.id.tranamots) as TextView
            serfee = views.findViewById<View>(R.id.serfees) as TextView
            isgct = views.findViewById<View>(R.id.isgcts) as TextView
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.settlemment_layout, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.btn_p_amounts!!.setText(format(mValues[position].aepsCount))
        viewHolder.btn_name!!.text = mValues[position].requestType
        viewHolder.p_transid!!.setText(format(mValues[position].aepsValue))
        viewHolder.btn_p_bank!!.setText(format(mValues[position].usage))
        viewHolder.agent_category!!.setText(mValues[position].transferAmount.toString())
        viewHolder.serfee!!.setText(format(mValues[position].serviceFee))
        viewHolder.isgct!!.text = mValues[position].iGST
    }
    private fun format(amount: String): String? {
        try {
            val formatter = NumberFormat.getInstance(Locale("en", "IN"))
            return formatter.format(formatter.parse(amount))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        mValues!!.clear()
        if (charText.length == 0) {
            mValues!!.addAll(arraylist!!)
        } else {
            for (wp in arraylist!!) {
                if (wp.requestType.toLowerCase(Locale.getDefault())
                                .contains(charText)||wp.usage.toLowerCase(Locale.getDefault())
                                .contains(charText)) {
                    mValues.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }
}