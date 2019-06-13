package com.rapipay.android.agent.kotlin_classs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.rapipay.android.agent.Model.SettlementPozoo
import com.rapipay.android.agent.R
import com.rapipay.android.agent.adapter.SettleAdapterBank
import com.rapipay.android.agent.interfaces.ClickListener
import com.rapipay.android.agent.interfaces.CustomInterface
import com.rapipay.android.agent.interfaces.RequestHandler
import com.rapipay.android.agent.utils.*
import com.rapipay.android.agent.view.EnglishNumberToWords
import me.grantland.widget.AutofitTextView
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class SettlementBak : BaseFragment(), RequestHandler {
    var transactionPozoArrayList: ArrayList<SettlementPozoo>? = null
    var trans_details: RecyclerView? = null
    protected var headerData = WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD
    var adapters: SettleAdapterBank? = null
    var pozoClick: SettlementPozoo? = null
    var textsss: TextView? = null
    var jsonObjects: JSONObject? = null
    var bankName: TextView? = null
    var accnum: TextView? = null
    var holdername: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.settle_lay, container, false) as View
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.details_Rapi)
            list = BaseCompactActivity.db.details
        init(view)
        loadUrl()
        return view
    }

    fun init(v: View) {
        holdername = v.findViewById<TextView>(R.id.holdername) as TextView
        accnum = v.findViewById<TextView>(R.id.accnum) as TextView
        bankName = v.findViewById<TextView>(R.id.bankName) as TextView
        var heading = v.findViewById<EditText>(R.id.headingsearch)
        heading?.onChange()
        trans_details = v.findViewById<RecyclerView>(R.id.trans_details)
        trans_details!!.addOnItemTouchListener(RecyclerTouchListener(activity, trans_details, object : ClickListener {
            override fun onClick(view: View?, position: Int) {
                if (btnstatus == false) {
                    btnstatus = true
                    pozoClick = transactionPozoArrayList!!.get(position)
                    if (pozoClick!!.transferAmount != 0)
                        customDialog_Ben(transactionPozoArrayList!!.get(position), "Network Transfer", "BENLAYOUT", "Transfer Amount")
                    else
                        Toast.makeText(activity, "Transfer Amount is not available for fund transfer.", Toast.LENGTH_SHORT).show()
                }
                handlercontrol()
            }

            override fun onLongClick(view: View?, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }))
    }

    fun loadUrl() {
        AsyncPostMethod(WebConfig.CRNF, getTransgerRequest().toString(), headerData, this, activity, getString(R.string.responseTimeOut)).execute()
    }

    override fun chechStat(`object`: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun chechStatus(`object`: JSONObject?) {
        try {
            if (`object`!!.has("responseCode") && `object`.getString("responseCode").equals("200", true)) {
                if (`object`.getString("serviceType").equals("CALCULATE_TRANFSER_AMOUNT", true)) {
                    jsonObjects = `object`
                    bankName!!.setText("Bank Name : " + `object`.getString("bankName"))
                    accnum!!.setText("Bank Account Number : " + `object`.getString("bankAccountNumber"))
                    holdername!!.setText("Account Holder Name : " + `object`.getString("bankAccountName"))
                    if (`object`.has("objTransferAmountList")) {
                        insertLastTransDetails(`object`.getJSONArray("objTransferAmountList"))
                    }

                } else if (`object`.getString("serviceType").equals("C2C_NETWORK_CREDIT", true)) {
                    customDialog_Ben(null, `object`.getString("responseMessage"), "NETWORK_CREDIT", "Credit Confirmation")
                }else if (`object`.getString("serviceType").equals("INITIATE_STLMNT_TRANSFER_FUND", true)) {
                    customDialog_Ben(null, `object`.getString("responseMessage"),"KYCLAYOUTS", "Alert")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun insertLastTransDetails(array: JSONArray) {
        transactionPozoArrayList = ArrayList<SettlementPozoo>()
        try {
            for (i in 0 until array.length()) {
                val `object` = array.getJSONObject(i)
                transactionPozoArrayList!!.add(SettlementPozoo(`object`.getString("requestType"), `object`.getString("aepsCount"), `object`.getString("aepsValue"), `object`.getString("usage"), `object`.getInt("transferAmount"), `object`.getString("serviceFee"), `object`.getString("iGST") + "/" + `object`.getString("cGST") + "/" + `object`.getString("sGST")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (transactionPozoArrayList!!.size != 0)
            initializeTransAdapter(transactionPozoArrayList!!)
    }

    private fun initializeTransAdapter(lists: ArrayList<SettlementPozoo>) {
        var layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        trans_details!!.setLayoutManager(layoutManager)
        adapters = SettleAdapterBank(activity as Context, lists, activity as Context)
        trans_details!!.setAdapter(adapters)
    }

    fun getTransgerRequest(): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "CALCULATE_TRANFSER_AMOUNT")
            jsonObject.put("requestType", "CRNF_CHANNEL")
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("sessionRefNo", list.get(0).aftersessionRefNo)
            jsonObject.put("nodeAgentId", list[0].mobilno)
            jsonObject.put("agentID", list[0].mobilno)
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    fun EditText.onChange() {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapters!!.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun customDialog_Ben(pozo: SettlementPozoo?, msg: String, type: String, title: String) {
        val btn_p_bank: AutofitTextView
        val btn_name: AutofitTextView
        val p_transid: AutofitTextView

        dialog = Dialog(activity)
        val inflater = activity!!.layoutInflater
        val alertLayout = inflater.inflate(R.layout.custom_layout_common, null)
        alertLayout.keepScreenOn = true
        val btn_cancel = alertLayout.findViewById<View>(R.id.btn_cancel) as AppCompatButton
        val btn_ok = alertLayout.findViewById<View>(R.id.btn_ok) as AppCompatButton
        val btn_regenerate = alertLayout.findViewById<View>(R.id.btn_regenerate) as AppCompatButton
        if (type.equals("BENLAYOUT", ignoreCase = true)) {
//            if (amount?.equals("null")||amount!!.equals("N", ignoreCase = true))
            btn_cancel.visibility = View.GONE
            btn_cancel.text = "Reverse transfer"
            btn_regenerate.text = "Cancel"
            btn_regenerate.textSize = 10f
            btn_regenerate.visibility = View.VISIBLE
            btn_cancel.textSize = 10f
            btn_ok.text = "Fund Transfer"
            btn_ok.textSize = 10f
            dialog.setContentView(alertLayout)
        } else if (type.equals("AMOUNTTRANSFER", ignoreCase = true)) {
            alertLayout.findViewById<View>(R.id.custom_popup).visibility = View.VISIBLE
            btn_name = alertLayout.findViewById<View>(R.id.btn_name_popup) as AutofitTextView
            p_transid = alertLayout.findViewById<View>(R.id.btn_p_transid) as AutofitTextView
            btn_p_bank = alertLayout.findViewById<View>(R.id.btn_p_bank) as AutofitTextView
            btn_name.text = "Request Type : " + pozo!!.requestType
            p_transid.text = "Usage : " + formatss(pozo.usage)!!
            btn_p_bank.text = "Transfer Amount : " + pozo.transferAmount
        } else if (type.equals("NETWORK_CREDIT", ignoreCase = true)) {
            btn_cancel.visibility = View.GONE
            val otpView = alertLayout.findViewById<View>(R.id.dialog_msg) as TextView
            otpView.text = msg
            otpView.visibility = View.VISIBLE
        } else if (type.equals("KYCLAYOUTS", ignoreCase = true)) {
            btn_cancel.visibility = View.GONE
            customView(alertLayout, msg, dialog)
        }
        textsss = alertLayout.findViewById<View>(R.id.input_amount_popup) as TextView
        val input_text = alertLayout.findViewById<View>(R.id.input_textss) as TextView
        textsss?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length != 0 && s.length < 10) {
                    input_text.text = EnglishNumberToWords().convert(Integer.parseInt(s.toString()))
                    input_text.visibility = View.VISIBLE
                } else
                    input_text.visibility = View.GONE
            }
        })
        dialog.setContentView(alertLayout)
        val texttitle = alertLayout.findViewById<View>(R.id.dialog_title) as TextView
        texttitle.text = title
        dialog.setCancelable(false)
        btn_ok.setOnClickListener {
            if (btnstatus == false) {
                btnstatus = true
                if (type.equals("AMOUNTTRANSFER", ignoreCase = true)) {
                    hideKeyboard(activity)
                    if (!ImageUtils.commonAmount(textsss!!.getText().toString())) {
                        textsss!!.setError("Please enter valid data")
                        textsss!!.requestFocus()
                    } else if (!(Integer.parseInt(textsss!!.getText().toString()) <= pozo!!.transferAmount!!.toInt())) {
                        textsss!!.setError("Please enter valid amount")
                        textsss!!.requestFocus()
                    } else {
                        dialog.dismiss()
                        customDialogConfirm(pozo, "Are you sure you want to Transfer?", "CONFIRMATION", textsss!!.getText().toString(), "Credit Confirmation")
                    }
                }
                if (type.equals("NETWORK_CREDIT", ignoreCase = true)) {
                    loadUrl()
                    dialog.dismiss()
                } else if (type.equals("BENLAYOUT", ignoreCase = true)) {
                    dialog.dismiss()
                    customDialog_Ben(pozo, "Network Transfer", "AMOUNTTRANSFER", "Fund Transfer")
                } else if (type.equals("KYCLAYOUTS", ignoreCase = true)) {
                    dialog.dismiss()
                    loadUrl()
                }
            }
            handlercontrol()
        }
        btn_cancel.setOnClickListener {
            if (btnstatus == false) {
                btnstatus = true
                if (type.equals("BENLAYOUT", ignoreCase = true)) {
                    dialog.dismiss()
                    customDialog_Ben(pozo, "Network Transfer", "REVERSETRANSFER", "Fund Transfer")
                } else
                    dialog.dismiss()
            }
            handlercontrol()
        }
        dialog.show()
        btn_regenerate.setOnClickListener { dialog.dismiss() }
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun customDialogConfirm(pozo: SettlementPozoo?, msg: String,
                                    type: String, amount: String, title: String) {
        dialognew = Dialog(activity!!)
        val inflater = activity!!.layoutInflater
        val alertLayout = inflater.inflate(R.layout.custom_layout_common, null)
        alertLayout.keepScreenOn = true
        val btn_cancel = alertLayout.findViewById<View>(R.id.btn_cancel) as AppCompatButton
        val btn_ok = alertLayout.findViewById<View>(R.id.btn_ok) as AppCompatButton
        if (type.equals("CONFIRMATION", ignoreCase = true)) {
            val otpView = alertLayout.findViewById<View>(R.id.dialog_msg) as TextView
            otpView.text = msg
            otpView.visibility = View.VISIBLE
        }
        dialognew.setContentView(alertLayout)
        val texttitle = alertLayout.findViewById<View>(R.id.dialog_title) as TextView
        texttitle.text = title
        dialognew.setCancelable(false)
        btn_ok.setOnClickListener {
            if (btnstatus == false) {
                btnstatus = true
                if (type.equals("CONFIRMATION", ignoreCase = true)) {
                    dialognew.dismiss()
                    AsyncPostMethod(WebConfig.CRNF, getNetwork_Transfer(pozo, amount).toString(), headerData, this@SettlementBak, activity, getString(R.string.responseTimeOut)).execute()
                }
            }
            handlercontrol()
        }
        btn_cancel.setOnClickListener { dialognew.dismiss() }
        dialognew.show()
        val window = dialognew.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun getNetwork_Transfer(pozo: SettlementPozoo?, txnAmount: String): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "INITIATE_STLMNT_TRANSFER_FUND")
            jsonObject.put("requestType", "BC_CHANNEL")
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("sessionRefNo", list.get(0).aftersessionRefNo)
            jsonObject.put("nodeAgentId", list[0].mobilno)
            jsonObject.put("agentID", list[0].mobilno)
            jsonObject.put("bankAccountName", jsonObjects!!.getString("bankAccountName"))
            jsonObject.put("accountNo", jsonObjects!!.getString("bankAccountNumber"))
            jsonObject.put("IFSC", jsonObjects!!.getString("bankIFSC"))
            jsonObject.put("reqFor", "STFT")
            jsonObject.put("txnAmmount", txnAmount)
            jsonObject.put("txnType", pozo!!.requestType)
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))

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