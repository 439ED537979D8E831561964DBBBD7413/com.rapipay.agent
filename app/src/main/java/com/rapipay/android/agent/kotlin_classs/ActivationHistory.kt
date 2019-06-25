package com.rapipay.android.agent.kotlin_classs

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.rapipay.android.agent.Model.ActiveDataList
import com.rapipay.android.agent.R
import com.rapipay.android.agent.adapter.ActivationHistoryAdapter
import com.rapipay.android.agent.interfaces.RequestHandler
import com.rapipay.android.agent.utils.*
import me.grantland.widget.AutofitTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class ActivationHistory : BaseFragment(), RequestHandler, View.OnClickListener {
    protected var headerData = WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD
    var transactionPozoArrayList: ArrayList<ActiveDataList>? = null
    var adapters: ActivationHistoryAdapter? = null
    var trans_details: ListView? = null
    protected var date2_text: AutofitTextView? = null
    protected var date1_text: AutofitTextView? = null
    protected var toimage: ImageView? = null
    protected var fromimage: ImageView? = null

    protected var selectedDate: Int = 0
    protected var selectedMonth: Int = 0
    protected var selectedYear: Int = 0
    var first = 1
    var last = 25
    internal var months: String? = null
    internal var dayss: String? = null

    var enterpin: EditText? = null
    var confirmpin: EditText? = null
    var btn_login: AppCompatButton? = null
    var belowlay: LinearLayout? = null
    var calendersss: LinearLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.enable_service_layout, container, false);
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.details_Rapi)
            list = BaseCompactActivity.dbRealm.details
        init(view)
        loadUrl()
        return view;
    }

    fun init(v: View) {
        trans_details = v.findViewById<ListView>(R.id.trans_details)
        val calendar = Calendar.getInstance()
        belowlay = v.findViewById<LinearLayout>(R.id.belowlay)
        calendersss = v.findViewById<LinearLayout>(R.id.calendersss)
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH)
        selectedMonth = calendar.get(Calendar.MONTH) + 1
        selectedYear = calendar.get(Calendar.YEAR)
        date2_text = v.findViewById<View>(R.id.date2) as AutofitTextView
        date1_text = v.findViewById<View>(R.id.date1) as AutofitTextView
        v.findViewById<View>(R.id.todate).setOnClickListener(toDateClicked)
        v.findViewById<View>(R.id.btn_fund).setOnClickListener(this)
        v.findViewById<View>(R.id.date1).setOnClickListener(toDateClicked)
        v.findViewById<View>(R.id.fromdate).setOnClickListener(fromDateClicked)
        v.findViewById<View>(R.id.date2).setOnClickListener(fromDateClicked)
        toimage = v.findViewById<View>(R.id.toimage) as ImageView
        toimage!!.setOnClickListener(toDateClicked)
//        toimage!!.setColorFilter(resources.getColor(R.color.colorPrimaryDark))
        fromimage = v.findViewById<View>(R.id.fromimage) as ImageView
        fromimage!!.setOnClickListener(fromDateClicked)
        date2_text!!.setText("$selectedYear-$selectedMonth-$selectedDate")
        date1_text!!.setText("$selectedYear-$selectedMonth-$selectedDate")
        enterpin = v.findViewById<View>(R.id.input_user) as EditText
        confirmpin = v.findViewById<View>(R.id.input_password) as EditText
        btn_login = v.findViewById<View>(R.id.btn_login) as AppCompatButton
        btn_login?.setOnClickListener(this)
//        fromimage!!.setColorFilter(resources.getColor(R.color.colorPrimaryDark))

    }

    protected var toDateClicked: View.OnClickListener = View.OnClickListener {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.datepickerview)
        dialog.setTitle("")

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker1)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH)
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedYear = calendar.get(Calendar.YEAR)
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { datePicker, year, month, dayOfMonth ->

            if ((month + 1).toString().length == 1)
                months = "0" + (month + 1).toString()
            else
                months = (month + 1).toString()
            if (dayOfMonth.toString().length == 1)
                dayss = "0$dayOfMonth"
            else
                dayss = dayOfMonth.toString()
            if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                date1_text!!.setText("$year-$months-$dayss")
                dialog.dismiss()
            } else {

                if (selectedDate != dayOfMonth) {
                    date1_text!!.setText("$year-$months-$dayss")
                    dialog.dismiss()
                } else {
                    if (selectedMonth != month) {
                        date1_text!!.setText("$year-$months-$dayss")
                        dialog.dismiss()
                    }
                }
            }
            date1_text!!.setError(null)
            selectedDate = dayOfMonth
            selectedMonth = month
            selectedYear = year
        }
        dialog.show()
    }
    protected var fromDateClicked: View.OnClickListener = View.OnClickListener {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.datepickerview)
        dialog.setTitle("")

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker1)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH)
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedYear = calendar.get(Calendar.YEAR)
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { datePicker, year, month, dayOfMonth ->

            if ((month + 1).toString().length == 1)
                months = "0" + (month + 1).toString()
            else
                months = (month + 1).toString()
            if (dayOfMonth.toString().length == 1)
                dayss = "0$dayOfMonth"
            else
                dayss = dayOfMonth.toString()
            if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                date2_text!!.setText("$year-$months-$dayss")
                dialog.dismiss()
            } else {

                if (selectedDate != dayOfMonth) {
                    date2_text!!.setText("$year-$months-$dayss")
                    dialog.dismiss()
                } else {
                    if (selectedMonth != month) {
                        date2_text!!.setText("$year-$months-$dayss")
                        dialog.dismiss()
                    }
                }
            }
            date2_text!!.setError(null)
            selectedDate = dayOfMonth
            selectedMonth = month
            selectedYear = year
        }
        dialog.show()
    }


    fun loadUrl() {
        AsyncPostMethod(WebConfig.SCRATCH_URL, getSubAgent().toString(), headerData, this, activity, getString(R.string.responseTimeOut), "ACTIVATIONSERVICE").execute()
    }

    fun getSubAgent(): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "ACTIVATION_HISTORY")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("nodeAgentId", list[0].mobilno)
            jsonObject.put("agentID", list[0].mobilno)
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("requestType", "COUPON_CHANNEL")
            jsonObject.put("sessionRefNo", list.get(0).aftersessionRefNo)
            jsonObject.put("fromDate", date2_text!!.getText().toString())
            jsonObject.put("toDate", date1_text!!.getText().toString())
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.btn_fund -> if (date2_text!!.getText().toString().isEmpty()) {
                date2_text!!.setError("Please enter mandatory field")
                date2_text!!.requestFocus()
            } else if (date1_text!!.getText().toString().isEmpty()) {
                date1_text!!.setError("Please enter mandatory field")
                date1_text!!.requestFocus()
            } else if (printDifference(mainDate(date2_text!!.getText().toString()), mainDate(date1_text!!.getText().toString()))) {
                loadUrl()
            } else {
                customDialog_Common("Statement can only view from one month")
            }
            R.id.btn_login -> {
                if (btnstatus == false) {
                    btnstatus = true
                    if (enterpin!!.text.toString().isEmpty()) {
                        enterpin?.error = "Please enter serial number"
                        enterpin?.requestFocus()
                    } else if (confirmpin!!.text.toString().isEmpty()) {
                        confirmpin?.error = "Please enter coupon number"
                        confirmpin?.requestFocus()
                    } else
                        AsyncPostMethod(WebConfig.SCRATCH_URL, getJson_Validate().toString(), headerData, this@ActivationHistory, getActivity(), getString(R.string.responseTimeOut), "ACTIVATIONSERVICE").execute()
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

    override fun chechStatus(`object`: JSONObject?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun chechStat(s: String?) {
        if (s != null) {
            try {
                val `object` = JSONObject(s)
                if (`object`!!.getString("responseCode").equals("200")) {
                    if (`object`.getString("serviceType").equals("ACTIVATION_HISTORY")) {
                        if (`object`.has("historyCount") && `object`.getString("historyCount").length != 0) {
                            insertLastTransDetails(`object`.getJSONArray("activeDataList"))
                        } else {
                            calendersss?.visibility = View.GONE
                            belowlay?.visibility = View.VISIBLE
                        }
                    } else if (`object`.getString("responseCode").equals("200", ignoreCase = true)) {
                        if (`object`.getString("serviceType").equals("ENABLE_SERVICES", ignoreCase = true)) {
                            customDialog_Ben("Alert", `object`.getString("responseMessage"))
                        }
                    }
                } else {
                    calendersss?.visibility = View.GONE
                    belowlay?.visibility = View.VISIBLE
                    responseMSg(`object`)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            calendersss?.visibility = View.GONE
            belowlay?.visibility = View.VISIBLE
        }
    }

    private fun insertLastTransDetails(array: JSONArray) {
        try {
            transactionPozoArrayList = ArrayList<ActiveDataList>()
            for (i in 0 until array.length()) {
                val `object` = array.getJSONObject(i)
                transactionPozoArrayList!!.add(ActiveDataList(`object`.getString("serialNumber"), `object`.getString("status"), `object`.getString("serviceName")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (transactionPozoArrayList!!.size != 0) {
            initializeTransAdapter(transactionPozoArrayList!!)
            calendersss?.visibility = View.VISIBLE
            belowlay?.visibility = View.GONE
        } else {
            calendersss?.visibility = View.GONE
            belowlay?.visibility = View.VISIBLE
        }
    }

    private fun initializeTransAdapter(list: ArrayList<ActiveDataList>) {
        adapters = ActivationHistoryAdapter(list, activity)
        trans_details!!.setAdapter(adapters)
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
            loadUrl()
            alertDialog?.dismiss()
        }
        btn_cancel.setOnClickListener { alertDialog?.dismiss() }
        alertDialog = dialog.show()
    }
}