package com.rapipay.android.agent.kotlin_classs

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import com.rapipay.android.agent.BuildConfig
import com.rapipay.android.agent.Model.BankDetailPozo
import com.rapipay.android.agent.R
import com.rapipay.android.agent.adapter.BankDetailAdapter
import com.rapipay.android.agent.interfaces.RequestHandler
import com.rapipay.android.agent.utils.*

import org.json.JSONObject
import java.nio.charset.Charset

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class BankDetails : BaseFragment(), RequestHandler {
    protected var headerData = WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD
    var detailPozoArrayList: ArrayList<BankDetailPozo>?=null
    var recycler_view: RecyclerView?=null
    var note1: TextView?=null
    var note2: TextView?=null

    val wlDetails: JSONObject
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date()
            val jsonObject = JSONObject()
            if (list.size != 0) {
                try {
                    jsonObject.put("serviceType", "WL_DOMAIN_DETAILS")
                    jsonObject.put("requestType", "HANDSET_CHANNEL")
                    jsonObject.put("typeMobileWeb", "mobile")
                    jsonObject.put("nodeAgentId", list[0].mobilno)
                    jsonObject.put("transactionID", ImageUtils.miliSeconds())
                    jsonObject.put("timeStamp", format.format(date))
                    jsonObject.put("appType", BuildConfig.USERTYPE)
                    jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].session, jsonObject.toString()))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                Toast.makeText(activity, "Blank Value", Toast.LENGTH_SHORT).show()
            }
            return jsonObject
        }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.bankdetail_layout, container, false);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.details_Rapi)
            list = BaseCompactActivity.db.details
        initialize(view)
        url()
        return view;
    }
//    public override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.bankdetail_layout)
//        initialize()
//        url()
//    }

    private fun initialize(view:View) {
//        heading = view.findViewById<View>(R.id.toolbar_title) as TextView
//        heading.text = "Bank Details for Deposit"
        recycler_view = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        note1 = view.findViewById<View>(R.id.note1) as TextView
        note2 = view.findViewById<View>(R.id.note2) as TextView
    }

    private fun url() {
        AsyncPostMethod(WebConfig.LOGIN_URL, wlDetails.toString(), headerData, this,activity, getString(R.string.responseTimeOut)).execute()
    }

    override fun chechStat(`object`: String) {

    }

    override fun chechStatus(`object`: JSONObject) {
        try {
            if (`object`.getString("responseCode").equals("200", ignoreCase = true)) {
                if (`object`.getString("serviceType").equals("WL_DOMAIN_DETAILS", ignoreCase = true)) {
                    val data = Base64.decode(`object`.getString("smsVirtualCode"), Base64.DEFAULT)
                    val text = String(data, Charsets.UTF_8)
                    parseBankDetails(JSONObject(text))

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun parseBankDetails(objects: JSONObject) {
        detailPozoArrayList = ArrayList()
        try {
            if (objects.has("NOTE1"))
                note1?.text = " Note1 : " + objects.getString("NOTE1")
            if (objects.has("NOTE2"))
                note2?.text = " Note2 : " + objects.getString("NOTE2")
            val array = objects.getJSONArray("BANK_DETAILS")
            for (i in 0 until array.length()) {
                val `object` = array.getJSONObject(i)
                if (`object`.getString("ECOL").equals("Y", ignoreCase = true))
                    detailPozoArrayList?.add(BankDetailPozo(`object`.getString("BANK"), `object`.getString("NAME"), `object`.getString("AC NO.") + list[0].mobilno, `object`.getString("BRANCH"), `object`.getString("IFSC"), `object`.getString("DEPOSIT"), `object`.getString("ECOL")))
                else
                    detailPozoArrayList?.add(BankDetailPozo(`object`.getString("BANK"), `object`.getString("NAME"), `object`.getString("AC NO."), `object`.getString("BRANCH"), `object`.getString("IFSC"), `object`.getString("DEPOSIT"), `object`.getString("ECOL")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (detailPozoArrayList!!.size != 0) {
            val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recycler_view?.layoutManager = layoutManager
            recycler_view?.adapter = BankDetailAdapter(activity, detailPozoArrayList)
        }

    }

}
