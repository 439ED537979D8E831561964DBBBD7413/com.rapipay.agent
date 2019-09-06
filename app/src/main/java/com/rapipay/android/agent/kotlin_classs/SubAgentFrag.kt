package com.rapipay.android.agent.kotlin_classs

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.rapipay.android.agent.Model.SubAgentList
import com.rapipay.android.agent.R
import com.rapipay.android.agent.adapter.SubAgentAdapter
import com.rapipay.android.agent.interfaces.ClickListener
import com.rapipay.android.agent.interfaces.CustomInterface
import com.rapipay.android.agent.interfaces.RequestHandler
import com.rapipay.android.agent.utils.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class SubAgentFrag : BaseFragment(), RequestHandler, CustomInterface, View.OnClickListener {
    protected var headerData = WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD
    var createagent: TextView? = null
    var trans_details: RecyclerView? = null
    lateinit var listsubAgent: ArrayList<SubAgentList>
    var pozo: SubAgentList? = null

    companion object {
        var mBroadcastStringAction: String = "com.rapipay.android.agent.fragments"
        fun getCount(): String {
            return mBroadcastStringAction
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.subagent_layout, container, false) as View
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.details_Rapi)
            list = BaseCompactActivity.dbRealm.details
        init(view)
        loadUrl()
        return view
    }

    fun loadUrl() {
        AsyncPostMethod(WebConfig.COMMONAPIS, getSubAgent().toString(), headerData, this, activity, getString(R.string.responseTimeOut)).execute();
    }

    fun init(v: View) {
        createagent = v.findViewById<TextView>(R.id.createagen)
        createagent!!.setOnClickListener(this);
        trans_details = v.findViewById<RecyclerView>(R.id.trans_details)
        trans_details!!.addOnItemTouchListener(RecyclerTouchListener(activity, trans_details, object : ClickListener {
            override fun onClick(view: View?, position: Int) {
                if (setClickable(view, isClickable) == false) {
                    isClickable = true
                } else {
                    isClickable = false
                    setClickable(view, isClickable)
                    pozo = listsubAgent[position]
                    customDialog_Common("SUBAGENTSERVICE", null, pozo, "Select Action", "", "", this@SubAgentFrag)
                }
            }

            override fun onLongClick(view: View?, position: Int) {
            }
        }))
    }

    var isClickable = true
    fun setClickable(view: View?, clickable: Boolean): Boolean {
        if (view != null) {
            if (view is ViewGroup) {
                val viewGroup = view as ViewGroup?
                for (i in 0 until viewGroup!!.childCount) {
                    setClickable(viewGroup.getChildAt(i), clickable)
                }
            }
            view.isClickable = clickable
        }
        return clickable
    }


    fun getSubAgent(): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "GET_SUB_AGENTS")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("nodeAgentId", list[0].mobilno)
            jsonObject.put("agentID", list[0].mobilno)
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("requestType", "REPORT_CHANNEL")
            jsonObject.put("sessionRefNo", list.get(0).aftersessionRefNo)
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    fun getUpdateSubAgent(pozo: SubAgentList): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "UPDATE_SUB_AGENTS")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("agentID", list[0].mobilno)
            jsonObject.put("nodeAgentId", pozo.subAgentMobNo)
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("requestType", "REPORT_CHANNEL")
            jsonObject.put("sessionRefNo", list.get(0).aftersessionRefNo)
            if (pozo.status.equals("N", true))
                jsonObject.put("operationFlag", "Y")
            else if (pozo.status.equals("Y", true))
                jsonObject.put("operationFlag", "N")
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    fun getUpdateAgentService(pozo: SubAgentList): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "GET_SUB_AGENTS_SERVICES")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("agentID", list[0].mobilno)
            jsonObject.put("nodeAgentId", pozo.subAgentMobNo)
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("requestType", "REPORT_CHANNEL")
            jsonObject.put("sessionRefNo", list.get(0).aftersessionRefNo)
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    fun getAgentService(pozo: SubAgentList): JSONObject {
        var activeIdList = ""
        for (i in arrayList.indices) {
            activeIdList = activeIdList + (arrayList.get(i) + ",")
        }
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "UPDATE_SUB_AGENT_SERVICES")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("agentID", list[0].mobilno)
            jsonObject.put("nodeAgentId", pozo.subAgentMobNo)
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("requestType", "REPORT_CHANNEL")
            jsonObject.put("sessionRefNo", list.get(0).aftersessionRefNo)
            jsonObject.put("subAgentSrvList", activeIdList)
            var increaselimit = increaselimit.text.toString()
            jsonObject.put("increaseLimit", increaselimit)
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }


    override fun onClick(v: View?) {
        var id: Int = v!!.id
        when (id) {
            R.id.createagen -> {
                createagent!!.setClickable(false)
                customDialog_Common("CREATEAGENT", null, null, "Create Sub-Agent", null, null, this@SubAgentFrag)
                createagent!!.setClickable(true)
            }
        }
    }

    public override fun clickable() {
        createagent!!.setClickable(true)
    }

    override fun onPause() {
        clickable()
        super.onPause()
    }

    override fun chechStat(`object`: String?) {
    }

    override fun chechStatus(`object`: JSONObject?) {
        try {
            if (`object`!!.getString("responseCode").equals("200")) {
                if (`object`.getString("serviceType").equals("GET_SUB_AGENTS")) {
                    initialiseData(`object`.getJSONArray("subAgentList"))
                } else if (`object`.getString("serviceType").equals("UPDATE_SUB_AGENTS")) {
                    customDialog_Common("KYCLAYOUTS", null, null, "Alert", "", `object`.getString("responseMessage"), this@SubAgentFrag)
                } else if (`object`.getString("serviceType").equals("CREATE_SUB_AGENTS", ignoreCase = true)) {
                    customDialog_Common("KYCLAYOUTSS", null, null, "Alert", null, `object`.getString("responseMessage"), this@SubAgentFrag)
                } else if (`object`.getString("serviceType").equals("GET_SUB_AGENTS_SERVICES", ignoreCase = true)) {
                    customDialog_List("UPDATESERVICE", `object`, null, "Permissions", null, null, this@SubAgentFrag)
                } else if (`object`.getString("serviceType").equals("UPDATE_SUB_AGENT_SERVICES", ignoreCase = true)) {
                    customDialog_Common("KYCLAYOUTSS", null, null, "Alert", null, `object`.getString("responseMessage"), this@SubAgentFrag)
                }
            } else if (`object`.getString("responseCode").equals("60147", ignoreCase = true)) run {
                Toast.makeText(context, `object`.getString("responseCode"), Toast.LENGTH_LONG).show()
                setBack_click1(context)
            } else
                responseMSg(`object`)
            createagent!!.setClickable(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initialiseData(array: JSONArray) {
        listsubAgent = ArrayList();
        try {
            for (i in 0 until array.length()) {
                var jsonObject = array.getJSONObject(i)
                listsubAgent.add(SubAgentList(jsonObject.getString("subAgentMobNo"), jsonObject.getString("status"), jsonObject.getString("subAgentName"), jsonObject.getString("currentLimit")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (listsubAgent.size != 0)
            initialiseAdapter(listsubAgent)
    }

    fun initialiseAdapter(list: ArrayList<SubAgentList>) {
        var layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        trans_details!!.setLayoutManager(layoutManager)
        var adapter = SubAgentAdapter(activity as Context, list)
        trans_details!!.setAdapter(adapter)
    }

    override fun okClicked(type: String?, ob: Any?) {
        if (type.equals("ACTIVATELAYOUT", true)) {
            var pozo = ob as SubAgentList
            AsyncPostMethod(WebConfig.COMMONAPIS, getUpdateSubAgent(pozo).toString(), headerData, this, activity, getString(R.string.responseTimeOut)).execute();
        } else if (type.equals("KYCLAYOUTS", true) || type.equals("KYCLAYOUTSS", true)) {
            loadUrl()
        } else if (type.equals("CREATEAGENT", ignoreCase = true)) {
            AsyncPostMethod(WebConfig.SUBAGENT, createAgent().toString(), headerData, this@SubAgentFrag, activity, getString(R.string.responseTimeOut)).execute()
        } else if (type.equals("SUBAGENTSERVICE", true)) {
            AsyncPostMethod(WebConfig.COMMONAPIS, getUpdateAgentService(ob as SubAgentList).toString(), headerData, this@SubAgentFrag, activity, getString(R.string.responseTimeOut)).execute()
        } else if (type.equals("UPDATESERVICE", true)) {
            AsyncPostMethod(WebConfig.COMMONAPIS, getAgentService(pozo!!).toString(), headerData, this@SubAgentFrag, activity, getString(R.string.responseTimeOut)).execute()
        }
    }

    fun createAgent(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "CREATE_SUB_AGENTS")
            jsonObject.put("requestType", "handset_CHannel")
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("txnRefId", ImageUtils.miliSeconds())
            jsonObject.put("firstName", first_name.getText().toString())
            jsonObject.put("lastName", last_name.getText().toString())
            jsonObject.put("address", cree_address.getText().toString())
            jsonObject.put("pinCode", pincode.getText().toString())
            jsonObject.put("state", bank_select.getText().toString())
            jsonObject.put("agentID", list[0].mobilno)
            jsonObject.put("nodeAgentId", mobile_num.getText().toString())
            jsonObject.put("sessionRefNo", list[0].aftersessionRefNo)
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }


    override fun cancelClicked(type: String?, ob: Any?) {
        if (type.equals("SUBAGENTSERVICE", true)) {
            var pozo = ob as SubAgentList;
            if (pozo.status.equals("N", true)) {
                customDialog_CommonNew("ACTIVATELAYOUT", null, pozo, "Activate Sub-Agent?", "Activate Sub-Agent", "Are you sure you want to Activate " + pozo.subAgentMobNo, this@SubAgentFrag)
            } else if (pozo.status.equals("Y", true)) {
                customDialog_CommonNew("ACTIVATELAYOUT", null, pozo, "Deactivate Sub-Agent?", "Deactivate Sub-Agent", "Are you sure you want to Deactivate " + pozo.subAgentMobNo, this@SubAgentFrag)
            }
        }
    }

}