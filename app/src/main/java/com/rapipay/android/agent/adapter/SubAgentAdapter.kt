package com.rapipay.android.agent.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rapipay.android.agent.Model.SubAgentList
import com.rapipay.android.agent.R
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SubAgentAdapter(private val context: Context,private val mValue: ArrayList<SubAgentList>) : RecyclerView.Adapter<SubAgentAdapter.ViewHolder>() {


    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val number: TextView
        val name: TextView
        val status: TextView
        val limit:TextView

        init {
            number = mView.findViewById<View>(R.id.number) as TextView
            name = mView.findViewById<View>(R.id.name) as TextView
            status = mView.findViewById<View>(R.id.status) as TextView
            limit = mView.findViewById<View>(R.id.limit)as TextView
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubAgentAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.subagent_adap_lay, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mValue.size
    }

    override fun onBindViewHolder(holder: SubAgentAdapter.ViewHolder, position: Int) {
        holder.number.text=mValue.get(position).subAgentMobNo
        holder.name.text=mValue.get(position).subAgentName
        holder.status.text=mValue.get(position).status
        holder.limit.text=formatss(mValue.get(position).currentLimit)
    }
    private fun formatss(amount: String): String? {
        try {
            val formatter = NumberFormat.getInstance(Locale("en", "IN"))
            return formatter.format(formatter.parse(amount))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}