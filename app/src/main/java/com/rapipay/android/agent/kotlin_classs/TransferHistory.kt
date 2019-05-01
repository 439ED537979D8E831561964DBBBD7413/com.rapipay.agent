package com.rapipay.android.agent.kotlin_classs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.rapipay.android.agent.Model.ChannelHistoryPozo
import com.rapipay.android.agent.R
import com.rapipay.android.agent.adapter.ChannelListAdapter
import com.rapipay.android.agent.interfaces.RequestHandler
import com.rapipay.android.agent.utils.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList

class TransferHistory : BaseFragment(), RequestHandler {
    protected var headerData = WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD
    var transactionPozoArrayList : ArrayList<ChannelHistoryPozo>? = null
    var reqFor: String? = null
    var adapters: ChannelListAdapter? = null
    var trans_details: ListView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.txn_history_layout, container, false);
        reqFor = arguments!!.getString("reqFor")
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.details_Rapi)
            list = BaseCompactActivity.db.details
        trans_details = view.findViewById<View>(R.id.trans_details) as ListView
        loadUrl()
        return view;
    }

    fun loadUrl() {
        AsyncPostMethod(WebConfig.CommonReport, getSubAgent().toString(), headerData, this, activity, getString(R.string.responseTimeOut)).execute()
    }

    fun getSubAgent(): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "GET_TXN_HISTORY_BY_SERVICE")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("nodeAgentId", list[0].mobilno)
            jsonObject.put("fromTxnDate", "")
            jsonObject.put("toTxnDate", "")
            jsonObject.put("fromIndex", 1)
            jsonObject.put("toIndex", 5)
            jsonObject.put("reqFor", reqFor)
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("requestType", "BC_CHANNEL")
            jsonObject.put("sessionRefNo", list.get(0).aftersessionRefNo)
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    override fun chechStat(`object`: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun chechStatus(`object`: JSONObject?) {
        try {
            if (`object`!!.getString("responseCode").equals("200")) {
                if (`object`.getString("serviceType").equals("GET_TXN_HISTORY_BY_SERVICE")) {
                    if (`object`.has("getTxnHistory")) {
                        insertLastTransDetails(`object`.getJSONArray("getTxnHistory"))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun insertLastTransDetails(array: JSONArray) {
        try {
            transactionPozoArrayList = ArrayList<ChannelHistoryPozo>()
            for (i in 0 until array.length()) {
                val `object` = array.getJSONObject(i)
                transactionPozoArrayList!!.add(ChannelHistoryPozo(`object`.getString("senderName") + " ( " + `object`.getString("mobileNo") + " )", `object`.getString("accountNo") + " ( " + `object`.getString("bankName") + " )", `object`.getString("requestAmt"), `object`.getString("txnStatus"), `object`.getString("txnDateTime"), `object`.getString("serviceProviderTXNID"), `object`.getString("transferType"), `object`.getString("userTxnId"), `object`.getString("serviceType"), `object`.getString("txnDateTime")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (transactionPozoArrayList!!.size != 0)
            initializeTransAdapter(transactionPozoArrayList!!)
    }

    private fun initializeTransAdapter(list: ArrayList<ChannelHistoryPozo>) {
        adapters = ChannelListAdapter(list, activity)
        trans_details!!.setAdapter(adapters)
    }
}