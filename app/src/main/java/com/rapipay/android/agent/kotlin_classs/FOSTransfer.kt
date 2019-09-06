package com.rapipay.android.agent.kotlin_classs

import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.rapipay.android.agent.Model.NetworkTransferPozo
import com.rapipay.android.agent.R
import com.rapipay.android.agent.adapter.NetworkTransferAdapter
import com.rapipay.android.agent.interfaces.RequestHandler
import com.rapipay.android.agent.utils.*
import me.grantland.widget.AutofitTextView
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class FOSTransfer : BaseFragment(), RequestHandler {
    var agentID: String? = null
    var nodeAgentID: String? = null
    var sessionRefNo: String? = null
    var sessionKey: String? = null
    var first = 1
    var last = 25
    private var isLoading: Boolean = false
    var transactionPozoArrayList: ArrayList<NetworkTransferPozo>? = null
    var trans_details: ListView? = null
    protected var headerData = WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD
    var adapters: NetworkTransferAdapter? = null
    var pozoClick: NetworkTransferPozo? = null
    var textsss: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fos_layout_transfer, container, false) as View
        init(view)
        loadUrl()
        return view
    }

    fun init(v: View) {
        var heading = v.findViewById<EditText>(R.id.headingsearch)
        heading?.onChange { "test" }
        nodeAgentID = arguments!!.getString("nodeAgentID")
        agentID = arguments!!.getString("AgentID")
        sessionRefNo = arguments!!.getString("sessionRefNo")
        sessionKey = arguments!!.getString("sessionKey")
        trans_details = v.findViewById<ListView>(R.id.trans_details)
        trans_details!!.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {

            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val lastInScreen = firstVisibleItem + visibleItemCount
                if (totalItemCount != 0 && totalItemCount == last && lastInScreen == totalItemCount && !isLoading) {
                    first = last + 1
                    last += 25
                    AsyncPostMethod(WebConfig.CommonReport, getTransgerRequest(first, last).toString(), headerData, this@FOSTransfer, activity, getString(R.string.responseTimeOut)).execute()
                    isLoading = true
                }
            }
        })
        trans_details!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            trans_details!!.setClickable(false)
            pozoClick = transactionPozoArrayList!!.get(position)
            customDialog_Ben(transactionPozoArrayList!!.get(position), "Network Transfer", "BENLAYOUT", pozoClick!!.getConsentStatus(), "Credit To Network")
        })
    }

    public override fun clickable() {
        trans_details!!.setClickable(true)
        btn_ok!!.setClickable(true)
        btn_cancel!!.setClickable(true)
    }

    override fun onPause() {
        clickable()
        super.onPause()
    }

    fun loadUrl() {
        AsyncPostMethod(WebConfig.CommonReport, getTransgerRequest(first, last).toString(), headerData, this, activity, getString(R.string.responseTimeOut)).execute()
    }

    override fun chechStat(`object`: String?) {
    }

    var s: String? = null
    override fun chechStatus(`object`: JSONObject?) {
        try {
            if (`object`!!.has("responseCode") && `object`.getString("responseCode").equals("200", true)) {
                if (`object`.getString("serviceType").equals("GET_MY_NODE_DETAILS", true)) {
                    if (`object`.has("objAgentNodeList")) {
                        s = formatss(`object`!!.getString("subAgentLimit")!!)
                        FOSMainActivity.setCount("Balance : " + s)
                        if (Integer.parseInt(`object`.getString("agentCount")) > 0) {
                            insertLastTransDetails(`object`.getJSONArray("objAgentNodeList"))
                        }
                    }

                } else if (`object`.getString("serviceType").equals("C2C_NETWORK_CREDIT", true)) {
                    customDialog_Ben(null, `object`.getString("responseMessage"), "NETWORK_CREDIT", null, "Credit Confirmation")
                }
            } else if (`object`.getString("responseCode").equals("60147", ignoreCase = true)) run {
                Toast.makeText(context, `object`.getString("responseCode"), Toast.LENGTH_LONG).show()
                setBack_click1(context)
            } else
                responseMSg(`object`)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun insertLastTransDetails(array: JSONArray) {
        if (array.length() != 1)
            transactionPozoArrayList = ArrayList<NetworkTransferPozo>()
        try {
            for (i in 0 until array.length()) {
                val `object` = array.getJSONObject(i)
                transactionPozoArrayList!!.add(NetworkTransferPozo(`object`.getString("companyName"), `object`.getString("mobileNo"), `object`.getString("agentName"), `object`.getString("agentBalance"), `object`.getString("agentCategory")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (transactionPozoArrayList!!.size != 0)
            initializeTransAdapter(transactionPozoArrayList!!)
    }

    private fun initializeTransAdapter(list: ArrayList<NetworkTransferPozo>) {
        if (first == 1) {
            adapters = NetworkTransferAdapter(activity, list)
            trans_details!!.setAdapter(adapters)
        } else {
            adapters!!.addAll(list)
            adapters!!.notifyDataSetChanged()
        }
        isLoading = false
    }

    fun getTransgerRequest(first: Int, last: Int): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "GET_MY_NODE_DETAILS")
            jsonObject.put("requestType", "BC_CHANNEL")
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("nodeAgentId", nodeAgentID)
            jsonObject.put("sessionRefNo", sessionRefNo)
            jsonObject.put("agentMobile", agentID)
            jsonObject.put("fromIndex", first)
            jsonObject.put("toIndex", last)
            jsonObject.put("checkSum", GenerateChecksum.checkSum(sessionKey, jsonObject.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    fun EditText.onChange(cb: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapters!!.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    var btn_ok: AppCompatButton? = null
    var btn_cancel: AppCompatButton? = null
    private fun customDialog_Ben(pozo: NetworkTransferPozo?, msg: String, type: String, amount: String?, title: String) {
        val btn_p_bank: AutofitTextView
        val btn_name: AutofitTextView
        val p_transid: AutofitTextView

        dialog = Dialog(activity)
        val inflater = activity!!.layoutInflater
        val alertLayout = inflater.inflate(R.layout.custom_layout_common, null)
        alertLayout.keepScreenOn = true
        btn_cancel = alertLayout.findViewById<View>(R.id.btn_cancel) as AppCompatButton
        btn_ok = alertLayout.findViewById<View>(R.id.btn_ok) as AppCompatButton
        val btn_regenerate = alertLayout.findViewById<View>(R.id.btn_regenerate) as AppCompatButton
        if (type.equals("BENLAYOUT", ignoreCase = true)) {
//            if (amount?.equals("null")||amount!!.equals("N", ignoreCase = true))
            btn_cancel!!.visibility = View.GONE
            btn_cancel!!.text = "Reverse transfer"
            btn_regenerate.text = "Cancel"
            btn_regenerate.textSize = 10f
            btn_regenerate.visibility = View.VISIBLE
            btn_cancel!!.textSize = 10f
            btn_ok!!.text = "Fund Transfer"
            btn_ok!!.textSize = 10f
            dialog.setContentView(alertLayout)
        } else if (type.equals("AMOUNTTRANSFER", ignoreCase = true)) {
            alertLayout.findViewById<View>(R.id.custom_popup).visibility = View.VISIBLE
            btn_name = alertLayout.findViewById<View>(R.id.btn_name_popup) as AutofitTextView
            p_transid = alertLayout.findViewById<View>(R.id.btn_p_transid) as AutofitTextView
            btn_p_bank = alertLayout.findViewById<View>(R.id.btn_p_bank) as AutofitTextView
            btn_name.text = "Company Name : " + pozo!!.companyName
            p_transid.text = pozo.agentName + " - " + pozo.mobileNo
            btn_p_bank.text = "Current Balance : " + formatss(pozo.agentBalance)!!
        } else if (type.equals("NETWORK_CREDIT", ignoreCase = true)) {
            btn_cancel!!.visibility = View.GONE
            val otpView = alertLayout.findViewById<View>(R.id.dialog_msg) as TextView
            otpView.text = msg
            otpView.visibility = View.VISIBLE
        }
        textsss = alertLayout.findViewById<View>(R.id.input_amount_popup) as TextView
        dialog.setContentView(alertLayout)
        val texttitle = alertLayout.findViewById<View>(R.id.dialog_title) as TextView
        texttitle.text = title
        dialog.setCancelable(false)
        btn_ok!!.setOnClickListener {
            btn_ok!!.setClickable(false)
            if (type.equals("AMOUNTTRANSFER", ignoreCase = true)) {
                hideKeyboard(activity)
                if (!ImageUtils.commonAmount(textsss!!.getText().toString())) {
                    textsss!!.setError("Please enter valid data")
                    textsss!!.requestFocus()
                } else if (!(Integer.parseInt(textsss!!.getText().toString()) <= Integer.parseInt(formatss(s!!)))) {
                    textsss!!.setError("Please enter valid amount")
                    textsss!!.requestFocus()
                } else {
                    dialog.dismiss()
                    customDialogConfirm(pozo, "Are you sure you want to Transfer?", "CONFIRMATION", textsss!!.getText().toString(), "", "Credit Confirmation")
                }
            }
            if (type.equals("NETWORK_CREDIT", ignoreCase = true)) {
                loadUrl()
                dialog.dismiss()
            } else if (type.equals("BENLAYOUT", ignoreCase = true)) {
                dialog.dismiss()
                customDialog_Ben(pozo, "Network Transfer", "AMOUNTTRANSFER", "", "Credit To Network")
            }
        }
        btn_cancel!!.setOnClickListener {
            btn_cancel!!.setClickable(false)
            if (type.equals("BENLAYOUT", ignoreCase = true)) {
                dialog.dismiss()
                customDialog_Ben(pozo, "Network Transfer", "REVERSETRANSFER", "", "Credit To Network")
            } else
                dialog.dismiss()
        }
        dialog.show()
        btn_regenerate.setOnClickListener { dialog.dismiss() }
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun customDialogConfirm(pozo: NetworkTransferPozo?, msg: String,
                                    type: String, amount: String, tpin: String, title: String) {
        dialognew = Dialog(activity!!)
        val inflater = activity!!.layoutInflater
        val alertLayout = inflater.inflate(R.layout.custom_layout_common, null)
        alertLayout.keepScreenOn = true
        val btn_cancel = alertLayout.findViewById<View>(R.id.btn_cancel) as AppCompatButton
        btn_ok = alertLayout.findViewById<View>(R.id.btn_ok) as AppCompatButton
        if (type.equals("CONFIRMATION", ignoreCase = true)) {
            val otpView = alertLayout.findViewById<View>(R.id.dialog_msg) as TextView
            otpView.text = msg
            otpView.visibility = View.VISIBLE
        }
        dialognew.setContentView(alertLayout)
        val texttitle = alertLayout.findViewById<View>(R.id.dialog_title) as TextView
        texttitle.text = title
        dialognew.setCancelable(false)
        btn_ok!!.setOnClickListener {
            btn_ok!!.setClickable(false)
            if (type.equals("CONFIRMATION", ignoreCase = true)) {
                dialognew.dismiss()
                AsyncPostMethod(WebConfig.CRNF, getNetwork_Transfer(pozo!!.mobileNo, amount, tpin).toString(), headerData, this@FOSTransfer, activity, getString(R.string.responseTimeOut)).execute()
            }
        }
        btn_cancel.setOnClickListener { dialognew.dismiss() }
        dialognew.show()
        val window = dialognew.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun getNetwork_Transfer(receiverId: String, txnAmount: String, newtpin: String): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "C2C_NETWORK_CREDIT")
            jsonObject.put("requestType", "BC_CHANNEL")
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("nodeAgentId", nodeAgentID)
            jsonObject.put("sessionRefNo", sessionRefNo)
            jsonObject.put("agentSenderID", agentID)
            jsonObject.put("agentReciverID", receiverId)
            jsonObject.put("subNetFlag", "Y")
            jsonObject.put("txnAmount", txnAmount)
            if (newtpin.isEmpty())
                jsonObject.put("tPin", "")
            else
                jsonObject.put("tPin", ImageUtils.encodeSHA256(newtpin))
            jsonObject.put("checkSum", GenerateChecksum.checkSum(sessionKey, jsonObject.toString()))

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonObject
    }

    protected fun formatss(amount: String): String? {
        try {
            val formatter = NumberFormat.getInstance(Locale("en", "IN"))
            return formatter.format(formatter.parse(amount))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}