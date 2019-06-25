package com.rapipay.android.agent.kotlin_classs

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.rapipay.android.agent.R
import com.rapipay.android.agent.interfaces.RequestHandler
import com.rapipay.android.agent.utils.*
import org.json.JSONObject

class ServiceEnable : BaseFragment(), View.OnClickListener, RequestHandler {

    var enterpin: EditText? = null
    var confirmpin: EditText? = null
    var btn_login: AppCompatButton? = null
    protected var headerData = WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rv = inflater.inflate(R.layout.enable_service_layout, container, false) as View
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.details_Rapi)
            list = BaseCompactActivity.dbRealm.details
        initialize(rv)
        return rv
    }

    private fun initialize(rv: View) {
        enterpin = rv.findViewById<View>(R.id.input_user) as EditText
        confirmpin = rv.findViewById<View>(R.id.input_password) as EditText
        btn_login = rv.findViewById<View>(R.id.btn_login) as AppCompatButton
        btn_login?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> {
                if (btnstatus == false) {
                    btnstatus = true
                    if (enterpin!!.text.toString().isEmpty()) {
                        enterpin?.error = "Please enter serial number"
                        enterpin?.requestFocus()
                    }else if (confirmpin!!.text.toString().isEmpty()) {
                        confirmpin?.error = "Please enter coupon number"
                        confirmpin?.requestFocus()
                    } else
                        AsyncPostMethod(WebConfig.SCRATCH_URL, getJson_Validate().toString(), headerData, this@ServiceEnable, getActivity(), getString(R.string.responseTimeOut)).execute()
                }
                handlercontrol()
            }
        }
    }

    fun getJson_Validate(): JSONObject {
        val jsonObject = JSONObject()
        if (list.size != 0) {
            try {
                jsonObject.put("serviceType", "ENABLE_SERVICES")
                jsonObject.put("requestType", "COUPON_CHANNEL")
                jsonObject.put("typeMobileWeb", "mobile")
                jsonObject.put("transactionID", ImageUtils.miliSeconds())
                jsonObject.put("agentID", list.get(0).getMobilno())
                jsonObject.put("nodeAgentId", list.get(0).getMobilno())
                jsonObject.put("serialNumber", enterpin?.text.toString())
                jsonObject.put("couponNumber", confirmpin?.text.toString())
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo())
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()))

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return jsonObject
    }

    override fun chechStat(`object`: String) {

    }

    override fun chechStatus(`object`: JSONObject) {
        try {
            if (`object`.getString("responseCode").equals("200", ignoreCase = true)) {
                if (`object`.getString("serviceType").equals("ENABLE_SERVICES", ignoreCase = true)) {
                    customDialog_Ben("Alert", `object`.getString("responseMessage"))
                }
            } else if (`object`.getString("responseCode").equals("75120", ignoreCase = true)) {
                if (`object`.getString("serviceType").equals("Txn_PIN_ENABLE", ignoreCase = true)) {
                    customDialog_Ben("Alert", `object`.getString("responseMessage"))
                }
            }else
                responseMSg(`object`)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    var alertDialog: AlertDialog? = null

    private fun customDialog_Ben(msg: String, title: String) {
        val dialog = AlertDialog.Builder(getActivity()!!)
        val inflater = getLayoutInflater()
        val alertLayout = inflater.inflate(R.layout.custom_layout_common, null)
        val btn_cancel = alertLayout.findViewById(R.id.btn_cancel) as AppCompatButton
        btn_cancel.visibility = View.GONE
        val btn_ok = alertLayout.findViewById(R.id.btn_ok) as AppCompatButton
        val otpView = alertLayout.findViewById(R.id.dialog_msg) as TextView
        otpView.text = title
        otpView.visibility = View.VISIBLE
        val texttitle = alertLayout.findViewById(R.id.dialog_title) as TextView
        texttitle.text = msg
        dialog.setView(alertLayout)
        dialog.setCancelable(false)
        btn_ok.setOnClickListener {
            enterpin?.setText("")
            enterpin?.hint = "Enter Serial Number*"
            confirmpin?.setText("")
            confirmpin?.hint = "Enter Coupon Number*"
            alertDialog?.dismiss()
        }
        btn_cancel.setOnClickListener { alertDialog?.dismiss() }
        alertDialog = dialog.show()
    }
}
